package ru.dimagor555.stocks.ui.allstocks;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import dagger.hilt.android.AndroidEntryPoint;
import ru.dimagor555.stocks.R;
import ru.dimagor555.stocks.ui.stockitem.StockItemAdapter;
import ru.dimagor555.stocks.ui.stocks.StocksViewModel;

@AndroidEntryPoint
public class AllStocksFragment extends Fragment {
    private RecyclerView recyclerViewStocks;

    private StocksViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_all_stocks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(getParentFragment()).get(StocksViewModel.class);
        recyclerViewStocks = view.findViewById(R.id.all_stocks_frag_recycler_view);

        initStocksRecyclerView();
        bindViewModel();
    }

    private void initStocksRecyclerView() {
        StockItemAdapter adapter = new StockItemAdapter(viewModel);
        recyclerViewStocks.setAdapter(adapter);
        ((SimpleItemAnimator) recyclerViewStocks.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    private void bindViewModel() {
        StockItemAdapter adapter = (StockItemAdapter) recyclerViewStocks.getAdapter();
        viewModel.getAllStocks().observe(getViewLifecycleOwner(), stockPagingData ->
                adapter.submitData(getLifecycle(), stockPagingData));

        viewModel.getErrors().observe(getViewLifecycleOwner(), errorModel -> {
            Context context = getContext();
            if (errorModel != null && context != null) {
                Toast.makeText(context,
                        errorModel.getTitle(context) + ": " + errorModel.getMessage(context),
                        Toast.LENGTH_LONG).show();
                viewModel.getErrors().setValue(null);
            }
        });
    }
}
