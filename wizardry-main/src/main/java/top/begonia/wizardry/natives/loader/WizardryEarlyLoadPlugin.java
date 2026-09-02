package top.begonia.wizardry.natives.loader;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import top.begonia.wizardry.Wizardry;

import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;

public class WizardryEarlyLoadPlugin implements IMixinConfigPlugin {
    private static final String LIB_NAME = "wizardry-native-libs";

    @Override
    public void onLoad(String mixinPackage) {
        Wizardry.LOGGER.info("当前类加载器: {}", this.getClass().getClassLoader());
        Wizardry.LOGGER.info("极早期 Mixin 插件触发，准备加载 DLL...");
        extractNativeLibrary();
        loadNativeLibrary();
    }

    private void loadNativeLibrary() {
        try {
            String libName = getNativeLibraryName();
            Path nativesDir = Path.of(System.getProperty("user.dir"), "natives");
            Path libPath = nativesDir.resolve(libName);
            File libFile = libPath.toFile();
            if (!libFile.exists()) {
                Wizardry.LOGGER.error("错误: 原生库不存在 -> {}", libPath);
                return;
            }

            Wizardry.LOGGER.info("正在加载: {}", libPath);
            System.load(libFile.getAbsolutePath());
            Wizardry.LOGGER.info("原生库加载成功！");

        } catch (UnsatisfiedLinkError e) {
            Wizardry.LOGGER.error("加载失败 (链接错误): ", e);
        } catch (Exception e) {
            Wizardry.LOGGER.error("加载过程中发生未知错误: ", e);
        }
    }

    private @NonNull String getNativeLibraryName() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "wizardry-native-libs.dll";
        }
        if (os.contains("linux") || os.contains("nix")) {
            return "libwizardry-native-libs.so";
        }
        if (os.contains("mac") || os.contains("darwin")) {
            return "libwizardry-native-libs.dylib";
        }
        throw new RuntimeException("不支持的操作系统: " + os);
    }

    private void extractNativeLibrary() {
        String nativesDirPath = System.getProperty("user.dir") + File.separator + "natives";
        File nativesDir = new File(nativesDirPath);
        if (!nativesDir.exists()) {
            boolean dirsCreated = nativesDir.mkdirs();
            if (dirsCreated) {
                Wizardry.LOGGER.info("已创建 natives 目录: {}", nativesDir.getAbsolutePath());
            }
        }
        String libFileName = System.mapLibraryName(LIB_NAME);
        String resourcePath = "/natives/" + libFileName;
        try (InputStream in = this.getClass().getResourceAsStream(resourcePath)) {
            if (in == null) {
                Wizardry.LOGGER.error("错误: 无法在 JAR 包内找到资源 {}", resourcePath);
                Wizardry.LOGGER.error("请检查 build.gradle 中的 copy 任务是否将文件放入了正确路径。");
                return;
            }
            File outputFile = new File(nativesDir, libFileName);
            String sourceHash = calculateSha256(in);
            if (sourceHash == null) {
                Wizardry.LOGGER.error("无法计算 JAR 包内原生库的哈希值");
                return;
            }
            if (outputFile.exists()) {
                String targetHash = calculateSha256(new FileInputStream(outputFile));
                if (sourceHash.equals(targetHash)) {
                    Wizardry.LOGGER.info("原生库哈希值匹配，跳过释放: {}", outputFile.getAbsolutePath());
                    return;
                } else {
                    Wizardry.LOGGER.info("原生库哈希值不匹配，正在覆盖更新...");
                }
            }
            try (InputStream reloadedIn = this.getClass().getResourceAsStream(resourcePath);
                 OutputStream out = new FileOutputStream(outputFile)) {

                if (reloadedIn == null) return; // 防御性编程

                byte[] buffer = new byte[8192]; // 适当调大缓冲区提升 IO 效率
                int bytesRead;
                while ((bytesRead = reloadedIn.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                Wizardry.LOGGER.info("成功释放原生库到: {}", outputFile.getAbsolutePath());
            }

        } catch (IOException e) {
            Wizardry.LOGGER.error("释放原生库时发生 IO 错误", e);
        }
    }

    private @Nullable String calculateSha256(InputStream inputStream) {
        try (InputStream is = inputStream) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest.digest()) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException | IOException e) {
            Wizardry.LOGGER.error("计算 SHA-256 哈希值失败", e);
            return null;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
