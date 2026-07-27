package top.begonia.wizardry.core.loader;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;

public class WizardryEarlyLoadPlugin implements IMixinConfigPlugin {
    private static boolean loaded = false;

    @Override
    public void onLoad(String mixinPackage) {
        if (loaded) return;

        System.out.println("当前类加载器: " + this.getClass().getClassLoader());
        System.out.println("[WizardryLoader] 极早期 Mixin 插件触发，准备加载 DLL...");

        try {
            InputStream in = getClass().getResourceAsStream("/assets/wizardry/natives/wizardry-native-libs.dll");
            if (in == null) {
                throw new RuntimeException("在资源目录中找不到 DLL 文件！");
            }

            Path tempDir = Files.createTempDirectory("wizardry_native_libs");
            File tempDll = new File(tempDir.toFile(), "wizardry-native-libs.dll");
            tempDll.deleteOnExit();

            Files.copy(in, tempDll.toPath(), StandardCopyOption.REPLACE_EXISTING);
            in.close();

            System.load(tempDll.getAbsolutePath());
            System.out.println("[WizardryLoader] DLL 极早期加载成功！路径: " + tempDll.getAbsolutePath());
            loaded = true;
        } catch (Exception e) {
            System.err.println("[WizardryLoader] DLL 加载失败！");
            e.printStackTrace();
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
