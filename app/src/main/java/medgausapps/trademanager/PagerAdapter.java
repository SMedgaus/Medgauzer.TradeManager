package medgausapps.trademanager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Sergey on 22.06.2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private final String[] tabNames;

    public PagerAdapter(FragmentManager fragmentManager, String[] tabNames) {
        super(fragmentManager);
        this.tabNames = tabNames;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ActualClientsFragment();
            case 1:
                return new OtherClientsFragment();
            case 2:
                return new AllClientsFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabNames.length;
    }

    public String[] getTabNames() {
        return tabNames;
    }

}
