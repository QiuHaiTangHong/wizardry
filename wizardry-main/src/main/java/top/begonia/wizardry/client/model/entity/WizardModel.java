package top.begonia.wizardry.client.model.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import top.begonia.wizardry.client.renderer.entity.state.WizardRenderState;

public class WizardModel extends HumanoidModel<WizardRenderState> {
    private final ModelPart beard;
    private final ModelPart cloak;

    protected WizardModel(ModelPart root) {
        super(root);
        this.beard = head.getChild("beard");
        this.cloak = body.getChild("cloak");
    }
}
