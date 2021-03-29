package ru.dimagor555.stocks.ui.search;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.dimagor555.stocks.R;
import ru.dimagor555.stocks.data.model.searchhistory.SearchHistoryRequest;

public class SearchHistoryItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView tvRequestText;

    public SearchHistoryItemViewHolder(@NonNull View itemView) {
        super(itemView);
        tvRequestText = itemView.findViewById(R.id.search_history_request_text);
    }

    public void bindOn(SearchHistoryRequest newItem) {
        clear();
        tvRequestText.setText(newItem.getRequestText());
    }

    public void clear() {
        tvRequestText.setText(null);
        setOnClickListener(null);
        setOnLongClickListener(null);
    }

    public void setOnClickListener(View.OnClickListener listener) {
        itemView.setOnClickListener(listener);
    }

    public void setOnLongClickListener(View.OnLongClickListener listener) {
        itemView.setOnLongClickListener(listener);
    }
}
