package ru.dimagor555.stocks.ui.favouritestocks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;
import ru.dimagor555.stocks.R;
import ru.dimagor555.stocks.ui.stockitem.StockItemAdapter;
import ru.dimagor555.stocks.ui.stocks.StocksViewModel;

@AndroidEntryPoint
public class FavouriteStocksFragment extends Fragment {
    private RecyclerView recyclerViewStocks;
    private LinearLayout favouriteListEmptyLayout;

    private StocksViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favourite_stocks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(getParentFragment()).get(StocksViewModel.class);
        recyclerViewStocks = view.findViewById(R.id.favourite_stocks_frag_recycler_view);
        favouriteListEmptyLayout = view.findViewById(R.id.favourite_stocks_frag_favorite_list_empty_layout);

        initStocksRecyclerView();
        bindViewModel();
    }

    private void initStocksRecyclerView() {
        StockItemAdapter adapter = new StockItemAdapter(viewModel);
        recyclerViewStocks.setAdapter(adapter);
    }

    private void bindViewModel() {
        StockItemAdapter adapter = (StockItemAdapter) recyclerViewStocks.getAdapter();
        viewModel.getFavouriteStocks().observe(getViewLifecycleOwner(), stockPagingData ->
                adapter.submitData(getLifecycle(), stockPagingData));

        viewModel.getFavouriteListEmpty().observe(getViewLifecycleOwner(), favouriteListEmpty -> {
            if (favouriteListEmpty) {
                favouriteListEmptyLayout.setVisibility(View.VISIBLE);
            } else {
                favouriteListEmptyLayout.setVisibility(View.INVISIBLE);
            }
        });
    }
}
