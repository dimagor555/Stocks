package ru.dimagor555.stocks.ui.stocks;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import ru.dimagor555.stocks.ui.allstocks.AllStocksFragment;
import ru.dimagor555.stocks.ui.favouritestocks.FavouriteStocksFragment;

public class StocksFragmentStateAdapter extends FragmentStateAdapter {
    private static final int FRAGMENT_COUNT = 2;
    private static final int ALL_STOCKS_FRAGMENT = 0;
    private static final int FAVOURITE_STOCKS_FRAGMENT = 1;

    public StocksFragmentStateAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment toReturn;

        switch (position) {
            case ALL_STOCKS_FRAGMENT:
                toReturn = new AllStocksFragment();
                break;
            case FAVOURITE_STOCKS_FRAGMENT:
                toReturn = new FavouriteStocksFragment();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + position);
        }

        return toReturn;
    }

    @Override
    public int getItemCount() {
        return FRAGMENT_COUNT;
    }
}
