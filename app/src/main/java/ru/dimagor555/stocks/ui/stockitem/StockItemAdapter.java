package ru.dimagor555.stocks.ui.stockitem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import org.jetbrains.annotations.NotNull;
import ru.dimagor555.stocks.R;
import ru.dimagor555.stocks.data.model.stock.Stock;
import ru.dimagor555.stocks.ui.StocksBaseViewModel;

public class StockItemAdapter extends PagingDataAdapter<Stock, StockItemViewHolder> {
    private final StocksBaseViewModel viewModel;

    public StockItemAdapter(StocksBaseViewModel viewModel) {
        super(COMPARATOR);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public StockItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_stock, parent, false);

        return new StockItemViewHolder(adapterLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull StockItemViewHolder holder, int position) {
        Stock currItem = getItem(position);
        if (currItem != null) {
            holder.bindOn(currItem);
            holder.setOnFavouriteClicked(v -> {
                currItem.toggleFavourite();
                viewModel.updateStockFavourite(currItem.getTicker(), currItem.isFavourite());
                currItem.toggleFavourite();
            });
            viewModel.notifyStockShownToUser(currItem);
        }
    }

    private static final DiffUtil.ItemCallback<Stock> COMPARATOR =
            new DiffUtil.ItemCallback<Stock>() {
        @Override
        public boolean areItemsTheSame(@NonNull @NotNull Stock oldItem,
                                       @NonNull @NotNull Stock newItem) {
            return oldItem.getTicker().equals(newItem.getTicker());
        }

        @Override
        public boolean areContentsTheSame(@NonNull @NotNull Stock oldItem,
                                          @NonNull @NotNull Stock newItem) {
            return oldItem.equals(newItem);
        }
    };
}
