package duelistmod.ui.configMenu.pages;

import basemod.IUIElement;
import basemod.ModLabel;
import basemod.ModLabeledToggleButton;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import duelistmod.DuelistMod;
import duelistmod.enums.DropdownMenuType;
import duelistmod.ui.configMenu.DuelistDropdown;
import duelistmod.ui.configMenu.SpecificConfigMenuPage;

import java.util.ArrayList;

public class General extends SpecificConfigMenuPage {

    public General() {
        super("General Settings");
    }

    public ArrayList<IUIElement> getElements() {
        String unlockString = DuelistMod.Config_UI_String.TEXT[8];
        ArrayList<IUIElement> settingElements = new ArrayList<>();

        settingElements.add(new ModLabeledToggleButton(unlockString, DuelistMod.xLabPos, DuelistMod.yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, DuelistMod.unlockAllDecks, DuelistMod.settingsPanel, (label) -> {}, (button) ->
        {
            DuelistMod.unlockAllDecks = button.enabled;
            try
            {
                SpireConfig config = new SpireConfig("TheDuelist", "DuelistConfig",DuelistMod.duelistDefaults);
                config.setBool(DuelistMod.PROP_UNLOCK, DuelistMod.unlockAllDecks);
                config.save();
            } catch (Exception e) { e.printStackTrace(); }

        }));

        settingElements.add(new ModLabeledToggleButton("Card Pool Relics", DuelistMod.xLabPos + DuelistMod.xSecondCol, DuelistMod.yPos, Settings.CREAM_COLOR, FontHelper.charDescFont, DuelistMod.allowCardPoolRelics, DuelistMod.settingsPanel, (label) -> {}, (button) ->
        {
            DuelistMod.allowCardPoolRelics = button.enabled;
            try
            {
                SpireConfig config = new SpireConfig("TheDuelist", "DuelistConfig",DuelistMod.duelistDefaults);
                config.setBool(DuelistMod.PROP_ALLOW_CARD_POOL_RELICS, DuelistMod.allowCardPoolRelics);
                config.save();
            } catch (Exception e) { e.printStackTrace(); }
        }));

        lineBreak();

        settingElements.add(new ModLabel("Birthday", DuelistMod.xLabPos, DuelistMod.yPos, DuelistMod.settingsPanel, (me)->{}));
        lineBreak();

        settingElements.add(new ModLabel("Month", DuelistMod.xLabPos, DuelistMod.yPos,DuelistMod.settingsPanel,(me)->{}));
        ArrayList<String> months = new ArrayList<>();
        months.add("---");
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
        DuelistDropdown monthSelector = new DuelistDropdown(months, DuelistMod.xLabPos + 270, DuelistMod.yPos + 22, DropdownMenuType.BIRTHDAY_MONTH);
        monthSelector.setSelectedIndex(DuelistMod.birthdayMonth > 0 && DuelistMod.birthdayMonth < 13 ? DuelistMod.birthdayMonth : 0);
        settingElements.add(monthSelector);

        settingElements.add(new ModLabel("Day", DuelistMod.xLabPos + DuelistMod.xSecondCol, DuelistMod.yPos,DuelistMod.settingsPanel,(me)->{}));
        ArrayList<String> days = new ArrayList<>();
        days.add("---");
        for (int i = 1; i < 32; i++) {
            days.add(i+"");
        }
        DuelistMod.daySelector = new DuelistDropdown(days, DuelistMod.xLabPos + DuelistMod.xSecondCol + 270, DuelistMod.yPos + 22, DropdownMenuType.BIRTHDAY_DAY);
        DuelistMod.daySelector.setSelectedIndex(DuelistMod.birthdayDay > 0 && DuelistMod.birthdayDay < 32 ? DuelistMod.birthdayDay : 0);
        settingElements.add(DuelistMod.daySelector);

        lineBreak();

        return settingElements;
    }
}
