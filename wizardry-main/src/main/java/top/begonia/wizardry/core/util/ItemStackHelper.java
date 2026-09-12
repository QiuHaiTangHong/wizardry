package top.begonia.wizardry.core.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.Equippable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import top.begonia.wizardry.Wizardry;
import top.begonia.wizardry.api.item.IManaStoringItem;
import top.begonia.wizardry.api.item.IWorkbenchItem;
import top.begonia.wizardry.core.config.ServerConfig;
import top.begonia.wizardry.core.constants.ElementEnum;
import top.begonia.wizardry.core.constants.TierEnum;
import top.begonia.wizardry.core.data.WandUpgradesData;
import top.begonia.wizardry.core.item.*;
import top.begonia.wizardry.core.registry.WizardryComponents;
import top.begonia.wizardry.core.registry.WizardryItems;
import top.begonia.wizardry.core.registry.WizardrySpells;
import top.begonia.wizardry.core.registry.WizardryTags;
import top.begonia.wizardry.core.spell.AbstractSpell;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 物品堆辅助工具类
 * <p> 提供与 <b> 魔杖 </b>,<b> 卷轴 </b> 和 <b> 盔甲 </b> 等模组物品相关的全套数据组件操作逻辑
 * <p> 主要功能包括:
 * <ul>
 *   <li><b> 法术管理 </b>: 获取, 设置, 选择和循环切换魔杖上的法术列表, 支持前后法术索引计算 </li>
 *   <li><b> 冷却控制 </b>: 读取和更新全体法术冷却数组, 递减冷却时间, 获取当前 / 上一位 / 下一位法术的剩余冷却 </li>
 *   <li><b> 升级增强 </b>: 注册和管理特殊魔杖升级物品 (如冷凝器, 存储, 虹吸等), 应用升级并查询升级等级 </li>
 *   <li><b> 装备工厂 </b>: 根据等级和元素创建魔杖实例; 根据元素, 材质分类和槽位类型生成完整数据驱动的盔甲实例 </li>
 *   <li><b> 属性访问 </b>: 读取和设置魔杖及盔甲的层级, 元素, 材质分类, 护甲类型等核心属性 </li>
 *   <li><b> 进度操作 </b>: 修改和查询魔杖的升级进度值 </li>
 *   <li><b> 显示名称 </b>: 为卷轴生成包含内部法术名称的本地化显示名 </li>
 * </ul>
 *
 * @author 秋海棠红
 * @since 1.0.0
 */
@SuppressWarnings("deprecation")
public final class ItemStackHelper {

    /**
     * 获取魔杖中存储的所有法术列表
     * <p> 从物品堆的数据组件中读取法术持有者列表, 并将其转换为抽象法术实例数组
     * <p> 如果物品堆中不存在法术数据组件, 则返回默认的空列表
     *
     * @param wand 需要获取法术列表的魔杖物品堆, 不能为 null
     * @return 包含所有法术的抽象法术数组, 如果无法术则返回空数组, 返回值不能为 null
     */
    public static AbstractSpell @NonNull [] getSpells(@NonNull ItemStack wand) {
        List<Holder<AbstractSpell>> spellHolders = wand.getOrDefault(
                WizardryComponents.SPELLS.get(),
                List.of()
        );
        return spellHolders.stream()
                .map(Holder::value)
                .toArray(AbstractSpell[]::new);
    }

    /**
     * 设置魔杖中存储的所有法术列表
     * <p> 将给定的抽象法术数组转换为法术持有者列表后, 写入物品堆的数据组件中
     * <p> 如果传入的法术数组为 {@code null} 或长度为 0, 则清空魔杖中的法术数据组件
     * <p> 法术转换过程中, 如果数组中的某个元素为 {@code null}, 则会使用默认的空法术 {@code WizardrySpells.NONE} 替代
     *
     * @param wand   需要设置法术列表的魔杖物品堆, 不能为 null
     * @param spells 要设置的抽象法术数组, 可以为 null 或空数组
     */
    public static void setSpells(ItemStack wand, AbstractSpell[] spells) {
        if (spells == null || spells.length == 0) {
            wand.set(WizardryComponents.SPELLS.get(), List.of());
            return;
        }
        Registry<AbstractSpell> registry = WizardrySpells.SPELLS.getRegistry().get();
        List<Holder<AbstractSpell>> spellHolders = Arrays.stream(spells)
                .map(spell -> {
                    AbstractSpell target = (spell != null) ? spell : WizardrySpells.NONE.get();
                    return registry.wrapAsHolder(target);
                })
                .toList();
        wand.set(WizardryComponents.SPELLS.get(), spellHolders);
    }

    @Contract("_, _ -> param1")
    public static @NonNull ItemStack setSpell(@NonNull ItemStack stack, @NonNull AbstractSpell spell) {
        stack.set(WizardryComponents.SPELL.get(), WizardrySpells.getHolder(spell.getIdentifier()));
        return stack;
    }

    /**
     * 获取魔杖当前选中的法术
     * <p> 从魔杖的物品堆数据组件中读取当前选中的法术索引, 并返回对应的法术实例
     * <p> 如果法术列表为空或当前索引越界, 则返回默认的空法术
     *
     * @param wand 需要获取当前法术的魔杖物品堆, 不能为 null
     * @return 当前选中的法术实例, 如果未能找到有效法术则返回 {@code WizardrySpells.NONE}, 返回值不能为 null
     */
    public static AbstractSpell getCurrentSpell(ItemStack wand) {
        AbstractSpell[] spells = getSpells(wand);
        int selectedSpell = wand.getOrDefault(WizardryComponents.CURRENT_SPELL.get(), 0);
        if (selectedSpell >= 0 && selectedSpell < spells.length) {
            return spells[selectedSpell];
        }
        return WizardrySpells.NONE.get();
    }

    /**
     * 获取魔杖中的下一个法术
     * <p> 根据当前选中的法术索引, 计算并返回下一个位置的法术实例
     * <p> 通过调用 {@code getNextSpellIndex} 获取下一个法术的索引, 然后在法术列表中查找对应的法术
     * <p> 索引计算规则为循环递增 (当前索引 + 1 后取模), 确保始终在有效范围内
     * <p> 如果法术列表为空或计算出的索引越界, 则返回默认的空法术
     *
     * @param wand 需要获取下一个法术的魔杖物品堆, 不能为 null
     * @return 下一个位置的法术实例, 如果未能找到有效法术则返回 {@code WizardrySpells.NONE}, 返回值不能为 null
     */
    public static AbstractSpell getNextSpell(ItemStack wand) {

        AbstractSpell[] spells = getSpells(wand);
        int index = getNextSpellIndex(wand);

        if (index >= 0 && index < spells.length) {
            return spells[index];
        }

        return WizardrySpells.NONE.get();
    }

    /**
     * 获取魔杖中的上一个法术
     * <p>根据当前选中的法术索引, 计算并返回上一个位置的法术实例
     * <p>通过调用 {@code getPreviousSpellIndex} 获取上一个法术的索引, 然后在法术列表中查找对应的法术
     * <p>索引计算规则为循环递减(当前索引 - 1 后取模), 确保始终在有效范围内
     * <p>如果法术列表为空或计算出的索引越界, 则返回默认的空法术
     *
     * @param wand 需要获取上一个法术的魔杖物品堆, 不能为 null
     * @return 上一个位置的法术实例, 如果未能找到有效法术则返回{@code WizardrySpells.NONE}, 返回值不能为 null
     */
    public static AbstractSpell getPreviousSpell(ItemStack wand) {

        AbstractSpell[] spells = getSpells(wand);
        int index = getPreviousSpellIndex(wand);

        if (index >= 0 && index < spells.length) {
            return spells[index];
        }

        return WizardrySpells.NONE.get();
    }

    /**
     * 获取卷轴的显示名称
     * <p> 从卷轴物品堆的数据组件中读取内部存储的法术信息, 并使用该法术的显示名称作为参数,
     * 生成包含本地化翻译文本的显示名称组件
     * <p> 如果卷轴中未存储法术数据, 则使用默认的空法术生成显示名称
     *
     * @param scroll 需要获取显示名称的卷轴物品堆, 不能为 null
     * @return 包含法术名称的本地化显示名称组件, 返回值不能为 null
     */
    public static @NonNull MutableComponent getScrollDisplayName(@NonNull ItemStack scroll) {
        AbstractSpell spell = scroll.getOrDefault(WizardryComponents.SPELL.get(), WizardrySpells.NONE).value();
        return Component.translatable("item." + Wizardry.MODID + ".scroll", spell.getDisplayName());
    }

    public static @NonNull AbstractSpell getNotWandSpell(@NonNull ItemStack stack) {
        return stack.getOrDefault(WizardryComponents.SPELL.get(), WizardrySpells.NONE).value();
    }

    /**
     * 选择魔杖中的下一个法术
     * <p>将当前选中的法术索引切换到下一个位置, 实现法术的循环切换
     * <p>如果魔杖尚未持有法术数据组件({@code SPELLS}), 则先初始化一个空的法术列表
     * <p>通过调用 {@code getNextSpellIndex} 计算下一个法术的索引(循环递增取模), 然后将该索引写入当前法术数据组件
     *
     * @param wand 需要切换法术的魔杖物品堆, 不能为 null
     */
    public static void selectNextSpell(@NonNull ItemStack wand) {
        if (!wand.has(WizardryComponents.SPELLS.get())) {
            wand.set(WizardryComponents.SPELLS.get(), new ArrayList<>());
        }
        int nextIndex = getNextSpellIndex(wand);
        wand.set(WizardryComponents.CURRENT_SPELL.get(), nextIndex);
    }

    /**
     * 选择魔杖中的上一个法术
     * <p>将当前选中的法术索引切换到上一个位置, 实现法术的循环反向切换
     * <p>通过调用 {@code getPreviousSpellIndex} 计算上一个法术的索引(循环递减取模), 然后将该索引写入当前法术数据组件
     * <p>注意: 此方法在法术数据组件不存在时不会进行初始化, 调用者需要确保魔杖已持有法术数据
     *
     * @param wand 需要切换法术的魔杖物品堆, 不能为 null
     */
    public static void selectPreviousSpell(@NonNull ItemStack wand) {
        if (!wand.has(WizardryComponents.SPELLS.get())) {
            setSpells(wand, new AbstractSpell[WandItem.BASE_SPELL_SLOTS.getAsInt()]);
        }
        wand.set(WizardryComponents.CURRENT_SPELL.get(), getPreviousSpellIndex(wand));
    }

    /**
     * 选择魔杖中指定索引位置的法术
     * <p> 根据传入的索引值, 将魔杖的当前选中法术切换到对应位置
     * <p> 在选择前会验证索引的有效性:
     * <ul>
     * <li> 检查魔杖是否已持有法术数据组件 (空检查体, 当前不做特殊处理)</li>
     * <li> 检查索引是否越界: 如果索引小于 0 或大于等于法术列表长度, 则操作失败并返回 false</li>
     * </ul>
     * <p> 如果索引有效, 则将索引写入当前法术数据组件并返回 true
     *
     * @param wand  需要切换法术的魔杖物品堆, 不能为 null
     * @param index 要选中的法术索引位置, 必须在 [0, 法术列表长度) 范围内
     * @return true 表示法术选择成功;false 表示索引无效, 选择操作未执行
     */
    public static boolean selectSpell(ItemStack wand, int index) {
        if (index < 0 || index >= getSpells(wand).length) {
            return false;
        }
        if (!wand.has(WizardryComponents.SPELLS.get())) {
            setSpells(wand, new AbstractSpell[WandItem.BASE_SPELL_SLOTS.getAsInt()]);
        }
        wand.set(WizardryComponents.CURRENT_SPELL.get(), index);
        return true;
    }

    /**
     * 计算魔杖中下一个法术的索引位置
     * <p> 根据当前选中的法术索引, 通过循环递增取模的方式计算下一个法术的索引
     * <p> 如果法术列表为空或仅包含一个法术, 则直接返回索引 0, 无需切换
     *
     * @param wand 需要计算下一个法术索引的魔杖物品堆, 不能为 null
     * @return 下一个法术的索引位置, 范围为 [0, 法术列表长度), 当法术数量不超过 1 时返回 0
     */
    private static int getNextSpellIndex(ItemStack wand) {
        int numberOfSpells = getSpells(wand).length;
        if (numberOfSpells <= 1) {
            return 0;
        }
        int currentIndex = wand.getOrDefault(WizardryComponents.CURRENT_SPELL.get(), 0);
        return (currentIndex + 1) % numberOfSpells;
    }

    /**
     * 计算魔杖中上一个法术的索引位置
     * <p> 根据当前选中的法术索引, 通过循环递减取模的方式计算上一个法术的索引
     * <p> 如果法术列表为空或仅包含一个法术, 则直接返回索引 0, 无需切换
     *
     * @param wand 需要计算上一个法术索引的魔杖物品堆, 不能为 null
     * @return 上一个法术的索引位置, 范围为 [0, 法术列表长度), 当法术数量不超过 1 时返回 0
     */
    private static int getPreviousSpellIndex(ItemStack wand) {
        int numberOfSpells = getSpells(wand).length;
        if (numberOfSpells <= 1) return 0;
        int currentIndex = wand.getOrDefault(WizardryComponents.CURRENT_SPELL.get(), 0);
        return (currentIndex - 1 + numberOfSpells) % numberOfSpells;
    }

    /**
     * 获取魔杖中所有法术的冷却时间数组
     * <p> 从物品堆的数据组件中读取全体法术冷却时间的整数列表, 并将其转换为基本类型 int 数组返回
     * <p> 如果物品堆中不存在冷却时间数据组件, 则返回默认的空列表转换的空数组
     *
     * @param wand 需要获取冷却时间数组的魔杖物品堆, 不能为 null
     * @return 包含所有法术冷却时间的 int 数组, 如果无冷却数据则返回空数组, 返回值不能为 null
     */
    public static int @NonNull [] getCooldowns(@NonNull ItemStack wand) {
        List<Integer> cooldownList = wand.getOrDefault(WizardryComponents.COOLDOWN_ARRAY_KEY.get(), List.of());
        int[] cooldowns = new int[cooldownList.size()];
        for (int i = 0; i < cooldownList.size(); i++) {
            cooldowns[i] = cooldownList.get(i);
        }
        return cooldowns;
    }

    /**
     * 设置魔杖中所有法术的冷却时间数组
     * <p> 将给定的 int 数组转换为整数列表后, 写入物品堆的数据组件中
     * <p> 如果传入的冷却时间数组为 {@code null}, 则移除魔杖中的冷却时间数据组件
     *
     * @param wand      需要设置冷却时间数组的魔杖物品堆, 不能为 null
     * @param cooldowns 要设置的全体法术冷却时间 int 数组, 可以为 null
     */
    public static void setCooldowns(ItemStack wand, int[] cooldowns) {
        if (cooldowns == null) {
            wand.remove(WizardryComponents.COOLDOWN_ARRAY_KEY.get());
            return;
        }
        List<Integer> list = new ArrayList<>(cooldowns.length);
        for (int cooldown : cooldowns) {
            list.add(cooldown);
        }
        wand.set(WizardryComponents.COOLDOWN_ARRAY_KEY.get(), list);
    }

    /**
     * 递减魔杖中所有法术的冷却时间
     * <p> 对魔杖中每个法术的剩余冷却时间进行递减操作, 每次调用将所有大于 0 的冷却值减 1,
     * 并将负数值校正为 0, 确保冷却时间数据始终处于有效范围内
     * <p> 如果魔杖当前没有冷却数据 (即冷却数组长度为 0), 则该方法直接返回不做任何处理
     *
     * @param wand 需要递减冷却时间的魔杖物品堆, 不能为 null
     */
    public static void decrementCooldowns(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        if (cooldowns.length == 0) return;
        for (int i = 0; i < cooldowns.length; i++) {
            if (cooldowns[i] > 0) cooldowns[i]--;
            if (cooldowns[i] < 0) cooldowns[i] = 0;
        }
        setCooldowns(wand, cooldowns);
    }

    /**
     * 获取魔杖当前选中法术的剩余冷却时间
     * <p> 从魔杖物品堆的数据组件中读取当前选中的法术索引, 然后在全体冷却时间数组中查询对应位置的冷却值
     * <p> 如果法术索引无效 (小于 0 或超出冷却数组范围), 则返回 0 表示无冷却
     *
     * @param wand 需要查询当前法术冷却时间的魔杖物品堆, 不能为 null
     * @return 当前选中法术的剩余冷却时间 (游戏刻), 如果索引无效则返回 0
     */
    public static int getCurrentCooldown(@NonNull ItemStack wand) {
        int selectedSpell = wand.getOrDefault(WizardryComponents.CURRENT_SPELL.get(), 0);
        int[] cooldowns = getCooldowns(wand);
        if (selectedSpell < 0 || selectedSpell >= cooldowns.length) {
            return 0;
        }
        return cooldowns[selectedSpell];
    }

    /**
     * 获取魔杖中下一个法术的剩余冷却时间
     * <p> 根据当前选中的法术索引计算出下一个法术的位置, 然后在全体冷却时间数组中查询该位置对应的冷却值
     * <p> 如果计算出的下一个法术索引无效 (小于 0 或超出冷却数组范围), 则返回 0
     *
     * @param wand 需要查询下一个法术冷却时间的魔杖物品堆, 不能为 null
     * @return 下一个法术的剩余冷却时间 (游戏刻), 如果索引无效则返回 0
     */
    public static int getNextCooldown(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        int nextSpell = getNextSpellIndex(wand);
        if (nextSpell < 0 || cooldowns.length <= nextSpell) {
            return 0;
        }
        return cooldowns[nextSpell];
    }

    /**
     * 获取魔杖中上一个法术的剩余冷却时间
     * <p> 根据当前选中的法术索引计算出上一个法术的位置, 然后在全体冷却时间数组中查询该位置对应的冷却值
     * <p> 如果计算出的上一个法术索引无效 (小于 0 或超出冷却数组范围), 则返回 0
     *
     * @param wand 需要查询上一个法术冷却时间的魔杖物品堆, 不能为 null
     * @return 上一个法术的剩余冷却时间 (游戏刻), 如果索引无效则返回 0
     */
    public static int getPreviousCooldown(ItemStack wand) {
        int[] cooldowns = getCooldowns(wand);
        int previousSpell = getPreviousSpellIndex(wand);
        if (previousSpell < 0 || cooldowns.length <= previousSpell) {
            return 0;
        }
        return cooldowns[previousSpell];
    }

    /**
     * 设置魔杖当前选中法术的冷却时间
     * <p> 直接为当前选中的法术设定指定的冷却时间值 (以游戏刻为单位), 并同步更新最大冷却时间数组.
     * 该方法会将冷却值钳制在至少 1 以上, 以确保法术始终处于有效的冷却状态.
     * <p> 如果冷却数组或最大冷却数组的长度小于法术列表长度, 则自动扩展数组以匹配当前的法术数量.
     * <p> 如果当前选中的法术索引无效 (小于 0 或超出法术列表范围), 则直接返回不执行任何操作.
     *
     * @param wand     需要设置冷却时间的魔杖物品堆, 不能为 null
     * @param cooldown 要设置的冷却时间值 (游戏刻), 最终取值会被钳制为 max(1, cooldown)
     */
    public static void setCurrentCooldown(@NonNull ItemStack wand, int cooldown) {
        int selectedSpell = wand.getOrDefault(WizardryComponents.CURRENT_SPELL.get(), 0);
        int spellCount = getSpells(wand).length;
        if (selectedSpell < 0 || selectedSpell >= spellCount) return;
        int[] cooldowns = getCooldowns(wand);
        if (cooldowns.length < spellCount) {
            cooldowns = java.util.Arrays.copyOf(cooldowns, spellCount);
        }
        int[] maxCooldowns = getMaxCooldowns(wand);
        if (maxCooldowns.length < spellCount) {
            maxCooldowns = java.util.Arrays.copyOf(maxCooldowns, spellCount);
        }
        int finalCooldown = Math.max(1, cooldown);
        cooldowns[selectedSpell] = finalCooldown;
        maxCooldowns[selectedSpell] = finalCooldown;
        setCooldowns(wand, cooldowns);
        setMaxCooldowns(wand, maxCooldowns);
    }

    /**
     * 获取魔杖中所有法术的最大冷却时间数组
     * <p> 从物品堆的数据组件中读取全体法术最大冷却时间的整数列表, 并将其转换为基本类型 int 数组返回
     * <p> 如果物品堆中不存在最大冷却时间数据组件, 则返回默认的空列表转换的空数组
     *
     * @param wand 需要获取最大冷却时间数组的魔杖物品堆, 不能为 null
     * @return 包含所有法术最大冷却时间的 int 数组, 如果无冷却数据则返回空数组, 返回值不能为 null
     */
    public static int[] getMaxCooldowns(@NonNull ItemStack wand) {
        List<Integer> list = wand.getOrDefault(WizardryComponents.MAX_COOLDOWN_ARRAY_KEY.get(), List.of());
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * 设置魔杖中所有法术的最大冷却时间数组
     * <p> 将给定的 int 数组转换为整数列表后, 写入物品堆的数据组件中
     * <p> 如果传入的最大冷却时间数组为 {@code null}, 则移除魔杖中的最大冷却时间数据组件
     *
     * @param wand      需要设置最大冷却时间数组的魔杖物品堆, 不能为 null
     * @param cooldowns 要设置的全体法术最大冷却时间 int 数组, 可以为 null
     */
    public static void setMaxCooldowns(ItemStack wand, int[] cooldowns) {
        if (cooldowns == null) {
            wand.remove(WizardryComponents.MAX_COOLDOWN_ARRAY_KEY.get());
            return;
        }
        List<Integer> list = java.util.stream.IntStream.of(cooldowns).boxed().toList();
        wand.set(WizardryComponents.MAX_COOLDOWN_ARRAY_KEY.get(), list);
    }

    /**
     * 获取当前选中法术的最大冷却时间
     * <p> 从魔杖物品堆的数据组件中读取所有法术的最大冷却时间数组, 并返回当前选中法术对应的最大冷却值
     * <p> 如果当前选中的法术索引无效 (小于 0 或超出最大冷却数组范围), 则返回 0
     *
     * @param wand 需要查询当前法术最大冷却时间的魔杖物品堆, 不能为 null
     * @return 当前选中法术的最大冷却时间, 如果索引无效则返回 0
     */
    public static int getCurrentMaxCooldown(@NonNull ItemStack wand) {
        int selectedSpell = wand.getOrDefault(WizardryComponents.CURRENT_SPELL.get(), 0);
        int[] maxCooldowns = getMaxCooldowns(wand);

        if (selectedSpell < 0 || selectedSpell >= maxCooldowns.length) return 0;
        return maxCooldowns[selectedSpell];
    }

    /**
     * 获取魔杖中指定升级物品的升级等级
     * <p> 从魔杖的物品堆数据组件中读取升级数据, 查询并返回特定升级物品对应的等级计数
     * <p> 如果魔杖尚未持有升级数据组件, 则使用空升级数据作为默认值, 此时升级等级为 0
     *
     * @param wand    需要查询升级等级的魔杖物品堆, 不能为 null
     * @param upgrade 要查询的升级物品实例, 不能为 null
     * @return 该升级物品在魔杖上的等级计数, 如果未应用该升级则返回 0
     */
    public static int getUpgradeLevel(@NonNull ItemStack wand, @NonNull Item upgrade) {
        WandUpgradesData upgrades = wand.getOrDefault(
                WizardryComponents.UPGRADES.get(),
                WandUpgradesData.EMPTY
        );
        return upgrades.counts().getOrDefault(upgrade.builtInRegistryHolder().getDelegate(), 0);
    }

    /**
     * 获取魔杖中所有升级的总次数
     * <p> 从魔杖的物品堆数据组件中读取完整的升级数据, 统计所有已应用升级项的等级计数总和
     * <p> 如果魔杖尚未持有升级数据组件, 则使用空升级数据作为默认值, 此时升级总次数为 0
     *
     * @param wand 需要查询升级总次数的魔杖物品堆, 不能为 null
     * @return 所有升级项等级计数的总和, 如果无升级数据则返回 0
     */
    public static int getTotalUpgrades(@NonNull ItemStack wand) {
        WandUpgradesData upgrades = wand.getOrDefault(
                WizardryComponents.UPGRADES.get(),
                WandUpgradesData.EMPTY
        );
        return upgrades.counts().values().stream()
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * 为魔杖应用一个升级物品
     * <p> 该方法在现有的魔杖升级数据基础上追加指定的升级项, 将升级物品的注册委托信息添加到魔杖的升级数据组件中
     * <p> 如果魔杖尚未持有升级数据组件, 则会使用空升级数据作为初始值, 然后执行合并操作
     * <p> 升级次数由 {@code WandUpgradesData} 内部进行计数管理, 每次调用该方法会增加对应升级物品的等级计数
     *
     * @param wand    需要应用升级的魔杖物品堆, 不能为 null
     * @param upgrade 要应用的升级物品实例, 可以接受任意物品, 但通常应为 {@code WandUpgradeItem} 类型的升级物品
     */
    public static void applyUpgrade(@NonNull ItemStack wand, Item upgrade) {
        wand.update(
                WizardryComponents.UPGRADES.get(),
                WandUpgradesData.EMPTY,
                current -> current.withUpgrade(upgrade.builtInRegistryHolder().getDelegate())
        );
    }

    /**
     * 判断物品是否为魔杖升级物品
     * <p> 通过检查物品实例是否属于 {@code WandUpgradeItem} 类型来确定其是否可用于魔杖升级
     *
     * @param upgrade 需要检查的物品实例, 不能为 null
     * @return true 表示该物品是魔杖升级物品;false 表示不是
     */
    public static boolean isWandUpgrade(Item upgrade) {
        return upgrade instanceof WandUpgradeItem;
    }

    /**
     * 获取物品堆中存储的特殊升级数据
     * <p> 从物品堆的数据组件中读取完整的魔杖升级数据, 包括所有已应用的升级项及其对应的等级计数
     * <p> 如果物品堆中尚未设置升级数据组件, 则返回默认的空升级数据实例
     *
     * @param stack 需要查询特殊升级数据的物品堆, 不能为 null
     * @return 包含所有升级计数信息的魔杖升级数据, 若未设置则返回 {@code WandUpgradesData.EMPTY}, 返回值不能为 null
     */
    public static @NonNull WandUpgradesData getSpecialUpgrades(@NonNull ItemStack stack) {
        return stack.getOrDefault(WizardryComponents.UPGRADES.get(), WandUpgradesData.EMPTY);
    }

    /**
     * 设置魔杖的升级进度值
     * <p> 将指定的进度值直接写入魔杖物品堆的数据组件中, 用于更新魔杖的升级进度
     *
     * @param wand        需要设置升级进度的魔杖物品堆, 不能为 null
     * @param progression 要设置的升级进度值
     */
    public static void setProgression(@NonNull ItemStack wand, int progression) {
        wand.set(WizardryComponents.PROGRESSION.get(), progression);
    }

    /**
     * 获取魔杖的升级进度值
     * <p> 从魔杖物品堆的数据组件中读取当前的升级进度值, 如果尚未设置则返回默认值 0
     *
     * @param wand 需要查询升级进度的魔杖物品堆, 不能为 null
     * @return 当前的升级进度值, 如果未设置则返回 0
     */
    public static int getProgression(@NonNull ItemStack wand) {
        return wand.getOrDefault(WizardryComponents.PROGRESSION.get(), 0);
    }

    /**
     * 增加魔杖的升级进度值
     * <p> 读取魔杖当前的升级进度值, 累加上指定的增量后回写, 用于推进魔杖的升级进度
     *
     * @param wand        需要增加升级进度的魔杖物品堆, 不能为 null
     * @param progression 要增加的进度值, 通常为正整数
     */
    public static void addProgression(ItemStack wand, int progression) {
        setProgression(wand, getProgression(wand) + progression);
    }

    /**
     * 判断在应用按钮按下时是否可以为魔杖充能
     * <p> 当前实现为硬编码的占位方法, 始终返回 false, 表明该功能尚未启用或处于待实现状态
     *
     * @param centre   中央槽位, 通常用于放置需要充能的魔杖物品堆, 不能为 null
     * @param crystals 水晶槽位, 通常用于放置提供充能能量的水晶物品堆, 不能为 null
     * @return 固定返回 false, 表示充能操作当前不生效
     */
    public static boolean rechargeManaOnApplyButtonPressed(@NonNull Slot centre, Slot crystals) {
        boolean changed = false;
        if (!(centre.getItem().getItem() instanceof IWorkbenchItem) || !(centre.getItem().getItem() instanceof IManaStoringItem iManaStoringItem)) {
            return false;
        }
        if (crystals.getItem() != ItemStack.EMPTY && !iManaStoringItem.isManaFull(centre.getItem())) {
            int chargeDepleted = iManaStoringItem.getManaCapacity(centre.getItem()) - iManaStoringItem.getMana(centre.getItem());
            int manaPerItem = crystals.getItem().getItem() instanceof IManaStoringItem ?
                    ((IManaStoringItem) crystals.getItem().getItem()).getMana(crystals.getItem()) :
                    crystals.getItem().getItem() instanceof MagicCrystalItem ? ServerConfig.Constants.manaPerCrystal : ServerConfig.Constants.manaPerShard;
            if (crystals.getItem().getItem() == WizardryItems.CRYSTAL_SHARD.get()) {
                manaPerItem = ServerConfig.Constants.manaPerShard;
            }
            if (crystals.getItem().getItem() == WizardryItems.GRAND_CRYSTAL.get()) {
                manaPerItem = ServerConfig.Constants.grandCrystalMana;
            }
            if (crystals.getItem().getCount() * manaPerItem < chargeDepleted) {
                iManaStoringItem.rechargeMana(centre.getItem(), crystals.getItem().getCount() * manaPerItem);
                crystals.getItem().shrink(crystals.getItem().getCount());

            } else {
                iManaStoringItem.setMana(centre.getItem(), iManaStoringItem.getManaCapacity(centre.getItem()));
                crystals.getItem().shrink((int) Math.ceil(((double) chargeDepleted) / manaPerItem));
            }
            changed = true;
        }
        return changed;
    }

    /**
     * 获取物品堆的等级属性
     * <p> 从物品堆的数据组件中读取当前的等级信息, 用于识别魔杖或盔甲等物品的品质层级
     * <p> 如果物品堆中未存储等级数据组件, 则返回默认的新手等级
     *
     * @param stack 需要查询等级属性的物品堆, 不能为 null
     * @return 物品堆当前绑定的等级枚举值, 如果未设置则返回 {@code TierEnum.NOVICE}, 返回值不能为 null
     */
    public static @NonNull TierEnum getTier(@NonNull ItemStack stack) {
        return stack.getOrDefault(WizardryComponents.TIER, TierEnum.NOVICE);
    }

    /**
     * 设置物品堆的等级属性
     * <p> 将指定的等级枚举值写入物品堆的数据组件中, 用于定义魔杖或盔甲等物品的品质层级
     *
     * @param stack 需要设置等级属性的物品堆, 不能为 null
     * @param tier  要设置的等级枚举值, 不能为 null
     */
    public static void setTier(@NonNull ItemStack stack, TierEnum tier) {
        stack.set(WizardryComponents.TIER, tier);
    }

    /**
     * 获取物品堆的元素核心属性
     * <p> 从物品堆的数据组件中读取已绑定的元素信息, 用于标识魔杖或盔甲等物品所对应的元素类型
     * <p> 如果物品堆中未存储元素数据组件, 则返回默认的魔法元素
     *
     * @param stack 需要查询元素属性的物品堆, 不能为 null
     * @return 物品堆当前绑定的元素枚举值, 如果未设置则返回 {@code ElementEnum.MAGIC}, 返回值不能为 null
     */
    public static @NonNull ElementEnum getElement(@NonNull ItemStack stack) {
        return stack.getOrDefault(WizardryComponents.ELEMENT, ElementEnum.MAGIC);
    }

    /**
     * 设置物品堆的元素核心属性
     * <p> 将指定的元素枚举值写入物品堆的数据组件中, 用于定义魔杖或盔甲等物品所绑定的元素属性
     *
     * @param stack   需要设置元素属性的物品堆, 不能为 null
     * @param element 要绑定的元素枚举值, 不能为 null
     */
    public static void setElement(@NonNull ItemStack stack, ElementEnum element) {
        stack.set(WizardryComponents.ELEMENT, element);
    }

    /**
     * 根据等级和元素创建一个新的魔杖物品堆
     * <p>该方法作为魔杖工厂, 构造一个全新的魔杖 {@link ItemStack} 实例, 并为其设置以下核心属性:
     * <ul>
     * <li><b>等级 ({@link TierEnum})</b>: 决定魔杖的品质层级</li>
     * <li><b>元素 ({@link ElementEnum})</b>: 绑定魔杖的元素核心属性</li>
     * <li><b>最大耐久度</b>: 基于等级的最大充能值, 作为初始化和最大耐久度设置</li>
     * </ul>
     * <p>注意: 该方法仅创建基础魔杖实例, 不包含法术列表等其他数据组件的初始化
     *
     * @param tier    魔杖的等级, 不能为 null
     * @param element 魔杖绑定的元素, 不能为 null
     * @return 包含完整等级, 元素和最大耐久度属性的魔杖物品堆, 返回值不能为 null
     */
    public static @NonNull ItemStack getWand(TierEnum tier, ElementEnum element) {
        ItemStack itemStack = new ItemStack(WizardryItems.WAND);
        ItemStackHelper.setTier(itemStack, tier);
        ItemStackHelper.setElement(itemStack, element);
        itemStack.set(DataComponents.MAX_DAMAGE, tier.getMaxCharge());
        return itemStack;
    }

    public static Set<Item> getSpecialUpgrades() {
        return BuiltInRegistries.ITEM.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .filter(item -> item instanceof WandUpgradeItem)
                .collect(Collectors.toSet());
    }

    public static @NotNull ItemStack getArcaneTome(TierEnum tier, int count) {
        ItemStack itemStack = new ItemStack(WizardryItems.ARCANE_TOME.get(), count);
        ItemStackHelper.setTier(itemStack, tier);
        return itemStack;
    }

    public static @NotNull ItemStack getSpellBook(@NonNull AbstractSpell spell, int count) {
        String modid = spell.getIdentifier().getNamespace();
        if (modid.equals(Wizardry.MODID)) {
            ItemStack itemStack = new ItemStack(WizardryItems.SPELL_BOOK.get(), count);
            return ItemStackHelper.setSpell(itemStack, spell);
        } else {
            Optional<Item> firstMatch = BuiltInRegistries.ITEM.entrySet()
                    .stream()
                    .map(Map.Entry::getValue)
                    .filter(v -> v instanceof SpellBookItem
                            && spell.applicableForItem(v)
                            && BuiltInRegistries.ITEM.getKey(v).getNamespace().equals(modid)
                    )
                    .findFirst();
            return firstMatch.map(item -> ItemStackHelper.setSpell(new ItemStack(item, count), spell))
                    .orElseGet(() -> ItemStackHelper.setSpell(new ItemStack(WizardryItems.SPELL_BOOK.get(), count), spell));
        }
    }

    public static @NotNull ItemStack getMagicCrystal(ElementEnum element, int count) {
        ItemStack itemStack = new ItemStack(WizardryItems.MAGIC_CRYSTAL.get(), count);
        ItemStackHelper.setElement(itemStack, element);
        return itemStack;
    }

    /**
     * 装备生成工厂。
     * <p>
     * 遵循 26.1.1 的现代化全面数据组件化标准。不再依赖硬编码，而是将材质基类、子元素、最大耐久度、
     * 可附魔状态、属性修饰符以及最重要的 {@link Equippable} 可穿戴组件(包含渲染资产 ID 映射)
     * 统一作为独立 Component 编译写入生成的 {@link ItemStack} 中.
     * </p>
     *
     * @param armourItem         模组盔甲物品基类实例
     * @param element            装备绑定的元素核心属性
     * @param armourMaterialType 装备所属的法袍骨骼材质分类
     * @param armorType          装备的具体槽位物理形态(HELMET, CHESTPLATE 等)
     * @return 包含完整数据驱动上下文组件、可直接给予玩家的 {@link ItemStack} 实例
     */
    public static @NonNull ItemStack generateArmour(
            WizardArmourItem armourItem,
            ElementEnum element,
            ArmourHelper.@NonNull ArmourMaterialType armourMaterialType,
            @NonNull ArmorType armorType
    ) {
        ItemStack itemStack = new ItemStack(armourItem);
        ArmorMaterial armorMaterial = armourMaterialType.getBuilder().build(element);
        itemStack.set(WizardryComponents.ARMOR_MATERIAL_TYPE, armourMaterialType);
        itemStack.set(WizardryComponents.ARMOR_TYPE, armorType);
        itemStack.set(WizardryComponents.ELEMENT, element);
        itemStack.set(DataComponents.MAX_DAMAGE, armorType.getDurability(armorMaterial.durability()));
        itemStack.set(DataComponents.MAX_STACK_SIZE, 1);
        itemStack.set(DataComponents.DAMAGE, 0);
        itemStack.set(DataComponents.ENCHANTABLE, new Enchantable(armorMaterial.enchantmentValue()));
        itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, armorMaterial.createAttributes(armorType));
        itemStack.set(DataComponents.EQUIPPABLE, Equippable.builder(armorType.getSlot()).setEquipSound(armorMaterial.equipSound()).setAsset(armorMaterial.assetId()).setAllowedEntities(EntityTypes.PLAYER).build());
        return itemStack;
    }

    /**
     * 获取物品堆对应的盔甲材质类型
     * <p> 从物品堆的数据组件中读取盔甲材质类型信息, 该类型定义了装备所使用的材质分类 (如布料, 皮革, 链甲等法袍骨骼材质)
     *
     * @param stack 需要查询盔甲材质类型的物品堆, 不能为 null
     * @return 物品堆对应的盔甲材质类型, 如果未设置则返回 null
     */
    public static ArmourHelper.ArmourMaterialType getArmourMaterialType(@NonNull ItemStack stack) {
        return stack.get(WizardryComponents.ARMOR_MATERIAL_TYPE);
    }

    /**
     * 获取物品堆对应的盔甲槽位类型
     * <p> 从物品堆的数据组件中读取盔甲类型信息, 该类型定义了装备对应的穿戴槽位 (如头盔, 胸甲, 护腿, 靴子等)
     *
     * @param stack 需要查询盔甲类型的物品堆, 不能为 null
     * @return 物品堆对应的盔甲槽位类型, 如果未设置则返回 null
     */
    public static ArmorType getArmorType(@NonNull ItemStack stack) {
        return stack.get(WizardryComponents.ARMOR_TYPE);
    }

    /**
     * 判断物品堆是否为书籍类型
     * <p> 通过检查物品堆是否属于预定义的书籍标签集合来确定其是否为书籍物品
     *
     * @param stack 需要检查的物品堆, 不能为 null
     * @return true 表示该物品属于书籍标签集合,false 表示不属于
     */
    public static boolean isBook(@NonNull ItemStack stack) {
        return stack.is(WizardryTags.BOOKS);
    }
}
