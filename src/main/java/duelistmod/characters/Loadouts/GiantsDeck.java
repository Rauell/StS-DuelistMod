package duelistmod.characters.Loadouts;

import java.util.ArrayList;

import duelistmod.characters.DuelistCustomLoadout;
import duelistmod.variables.Strings;

public class GiantsDeck extends DuelistCustomLoadout
{
    public GiantsDeck()
    {
        this.ID = 18;
        this.Name = Strings.configGiantDeck;
        this.cardCount = "?? Cards";
        this.permaLockMessage = "Unavailable";
    }

    @Override
    public ArrayList<String> GetStartingDeck()
    {
        ArrayList<String> res = super.GetStartingDeck();
        // grab starter deck from defaultmod -> get arraylist of cards -> for each card in list add card.getID() to res
        return res;
    }
}
