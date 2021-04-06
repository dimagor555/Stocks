package ru.dimagor555.stocks.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import dagger.hilt.android.AndroidEntryPoint;
import ru.dimagor555.stocks.R;
import ru.dimagor555.stocks.ui.stockitem.StockItemAdapter;

@AndroidEntryPoint
public class SearchFragment extends Fragment {
    private TextInputLayout etLayoutSearch;
    private TextInputEditText etSearch;
    private RecyclerView recyclerViewStocks;
    private ConstraintLayout searchHistoryLayout;
    private RecyclerView recyclerViewHistory;
    private CircularProgressIndicator progressIndicator;
    private TextView tvNothingFound;

    private SearchViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        etLayoutSearch = view.findViewById(R.id.search_text_field_layout);
        etSearch = view.findViewById(R.id.search_text_field);
        recyclerViewStocks = view.findViewById(R.id.search_frag_stocks_recycler_view);
        searchHistoryLayout = view.findViewById(R.id.search_frag_search_history_layout);
        recyclerViewHistory = view.findViewById(R.id.search_frag_history_recycler_view);
        progressIndicator = view.findViewById(R.id.search_frag_progress_indicator);
        tvNothingFound = view.findViewById(R.id.search_frag_nothing_found_text);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        initSearchEditTextLayout();
        initSearchEditText();
        initRecyclerViews();
        requestFocusWithKeyboardOnSearchEditText();
        bindViewModel();
    }

    private void initSearchEditTextLayout() {
        etLayoutSearch.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etLayoutSearch.setStartIconDrawable(R.drawable.outline_arrow_back_24);
                etLayoutSearch.setStartIconOnClickListener(Navigation
                        .createNavigateOnClickListener(R.id.action_search_dest_to_stocks_dest));
            } else {
                etLayoutSearch.setStartIconDrawable(R.drawable.outline_search_24);
                etLayoutSearch.setStartIconOnClickListener(null);
            }
        });
    }

    private void initSearchEditText() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.onSearchFieldUpdated(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initRecyclerViews() {
        recyclerViewStocks.setAdapter(new StockItemAdapter(viewModel));
        ((SimpleItemAnimator) recyclerViewStocks.getItemAnimator()).setSupportsChangeAnimations(false);

        recyclerViewHistory.setLayoutManager(new StaggeredGridLayoutManager(4,
                StaggeredGridLayoutManager.HORIZONTAL));
        recyclerViewHistory.setAdapter(new SearchHistoryItemAdapter(viewModel));
    }

    private void requestFocusWithKeyboardOnSearchEditText() {
        etSearch.requestFocus();
        etSearch.getOnFocusChangeListener().onFocusChange(etSearch, true);

        InputMethodManager imn = ((InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE));
        imn.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
    }

    private void bindViewModel() {
        StockItemAdapter adapter = (StockItemAdapter) recyclerViewStocks.getAdapter();
        viewModel.getSearchResult().observe(getViewLifecycleOwner(), stockPagingData ->
                adapter.submitData(getLifecycle(), stockPagingData));

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading) {
                progressIndicator.show();
            } else {
                progressIndicator.hide();
            }
        });

        viewModel.getErrors().observe(getViewLifecycleOwner(), errorModel -> {
            Context context = getContext();
            if (errorModel != null && context != null) {
                Toast.makeText(context,
                        errorModel.getTitle(context) + ": " + errorModel.getMessage(context),
                        Toast.LENGTH_LONG).show();
                viewModel.getErrors().setValue(null);
            }
        });

        viewModel.getNothingFound().observe(getViewLifecycleOwner(), nothingFound -> {
            if (nothingFound) {
                tvNothingFound.setVisibility(View.VISIBLE);
            } else {
                tvNothingFound.setVisibility(View.GONE);
            }
        });

        viewModel.getEmptySearch().observe(getViewLifecycleOwner(), emptySearch -> {
            if (emptySearch) {
                recyclerViewStocks.setVisibility(View.GONE);
                searchHistoryLayout.setVisibility(View.VISIBLE);
            } else {
                recyclerViewStocks.setVisibility(View.VISIBLE);
                searchHistoryLayout.setVisibility(View.GONE);
            }
        });

        SearchHistoryItemAdapter historyAdapter =
                (SearchHistoryItemAdapter) recyclerViewHistory.getAdapter();
        viewModel.getSearchHistory().observe(getViewLifecycleOwner(), historyAdapter::submitData);

        viewModel.getSearchText().observe(getViewLifecycleOwner(), text -> {
            if (text != null) {
                etSearch.setText(text);
                viewModel.getSearchText().setValue(null);
            }
        });
    }
}
