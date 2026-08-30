package top.begonia.wizardry.core.commond;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.client.renderer.BlinkEffectRenderer;

public class DebugCommond {
    private static final SuggestionProvider<CommandSourceStack> SHADER_SUGGESTIONS = (_, builder) -> {
        var resourceManager = Minecraft.getInstance().getResourceManager();
        var shaders = resourceManager.listResources("post_effect", location -> location.getPath().endsWith(".json"))
                .keySet()
                .stream()
                .map(location -> {
                    String path = location.getPath();
                    String cleanPath = path.substring("post_effect/".length(), path.length() - ".json".length());
                    return Identifier.fromNamespaceAndPath(location.getNamespace(), cleanPath);
                });

        return SharedSuggestionProvider.suggestResource(shaders, builder);
    };

    public static void register(@NonNull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("post_effect")
                        .requires(source -> source.permissions().hasPermission(new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)))
                        .then(
                                Commands.literal("set")
                                        .then(Commands.argument("effect_id", IdentifierArgument.id())
                                                .suggests(SHADER_SUGGESTIONS)
                                                .executes(context ->
                                                        applyPostEffect(context.getSource(), IdentifierArgument.getId(context, "effect_id")))
                                        )
                        )
                        .then(Commands.literal("clear")
                                .executes(context -> clearPostEffect(context.getSource()))
                        )
        );
    }

    private static int applyPostEffect(CommandSourceStack source, Identifier identifier) {
        Minecraft.getInstance().execute(() -> {
            try {
                BlinkEffectRenderer.playBlinkEffect();
                Minecraft.getInstance().gameRenderer.setPostEffect(identifier);
                source.sendSuccess(() -> Component.literal("§a已应用后处理效果: " + identifier), false);
            } catch (Exception e) {
                Minecraft.getInstance().gameRenderer.clearPostEffect();
                Wizardry.LOGGER.error("加载 Shader 失败 ({})，已自动还原", identifier, e);
                source.sendFailure(Component.literal("§c该 Shader 缺少必要的 Render Target，无法独立应用！"));
            }
        });
        return 1;
    }

    private static int clearPostEffect(CommandSourceStack source) {
        Minecraft.getInstance().execute(() -> {
            Minecraft.getInstance().gameRenderer.clearPostEffect();
            source.sendSuccess(() -> Component.literal("§a已清除后处理效果"), false);
        });

        return 1;
    }
}
