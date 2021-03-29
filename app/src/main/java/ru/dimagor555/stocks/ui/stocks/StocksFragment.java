package ru.dimagor555.stocks.ui.stocks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.android.material.textfield.TextInputEditText;
import ru.dimagor555.stocks.Injection;
import ru.dimagor555.stocks.R;

public class StocksFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_stocks, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initViewPager(view);
        initProgressIndicator(view);
        initSearchTextField(view);
    }

    private void initViewPager(View view) {
        ViewPager2 viewPager = view.findViewById(R.id.stocks_frag_pager);
        viewPager.setAdapter(new StocksFragmentStateAdapter(this));

        AppBarLayout appBarLayout = view.findViewById(R.id.stocks_frag_app_bar_layout);
        appBarLayout.addOnOffsetChangedListener((appBarLayout1, verticalOffset) ->
                viewPager.setY(appBarLayout1.getHeight() + verticalOffset));

        TabLayout tabLayout = view.findViewById(R.id.stocks_frag_tab_layout);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(getTabTitle(position))).attach();
    }

    private void initProgressIndicator(View view) {
        CircularProgressIndicator progressIndicator = view
                .findViewById(R.id.all_stocks_frag_progress_indicator);

        StocksViewModel viewModel = Injection.provideStocksViewModel(this);
        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            if (!loading) {
                progressIndicator.hide();
            }
        });
    }

    private void initSearchTextField(View view) {
        TextInputEditText searchTextField = view.findViewById(R.id.search_text_field);
        searchTextField.setClickable(true);
        searchTextField.setFocusable(false);
        searchTextField.setFocusableInTouchMode(false);
        searchTextField.setCursorVisible(false);

        searchTextField.setOnClickListener(Navigation
                .createNavigateOnClickListener(R.id.action_stocks_dest_to_search_dest));
    }

    private int getTabTitle(int position) {
        return position == 0 ? R.string.stocks : R.string.favourite;
    }
}
