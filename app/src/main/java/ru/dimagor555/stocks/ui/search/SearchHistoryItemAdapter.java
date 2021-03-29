package ru.dimagor555.stocks.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.dimagor555.stocks.R;
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRequest;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryItemAdapter extends RecyclerView.Adapter<SearchHistoryItemViewHolder> {
    private List<SearchHistoryRequest> items = new ArrayList<>();

    private final SearchViewModel viewModel;

    public SearchHistoryItemAdapter(SearchViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public SearchHistoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View adapterLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_search_history, parent, false);

        return new SearchHistoryItemViewHolder(adapterLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryItemViewHolder holder, int position) {
        SearchHistoryRequest currItem = items.get(position);
        if (currItem != null) {
            holder.bindOn(currItem);
            holder.setOnClickListener(v ->
                    viewModel.onSelectSearchRequestFromHistory(currItem));
            holder.setOnLongClickListener(v ->
                    viewModel.onDeleteSearchRequestFromHistory(currItem));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void submitData(List<SearchHistoryRequest> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}
