package ru.dimagor555.stocks.ui.stockitem;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import ru.dimagor555.stocks.R;
import ru.dimagor555.stocks.data.model.stock.Stock;
import ru.dimagor555.stocks.data.model.stock.StockCompanyInfo;
import ru.dimagor555.stocks.data.model.stock.StockPrice;

public class StockItemViewHolder extends RecyclerView.ViewHolder {
    private final ShapeableImageView ivStockIcon;
    private final TextView tvTicker;
    private final TextView tvCompanyName;
    private final TextView tvCurrPrice;
    private final TextView tvDeltaPrice;
    private final ImageView ivFavouriteIcon;
    private final View layoutBasicInfo;

    public StockItemViewHolder(@NonNull View itemView) {
        super(itemView);

        ivStockIcon = itemView.findViewById(R.id.stock_image_icon);
        tvTicker = itemView.findViewById(R.id.stock_text_ticker);
        tvCompanyName = itemView.findViewById(R.id.stock_text_company);
        tvCurrPrice = itemView.findViewById(R.id.stock_text_price);
        tvDeltaPrice = itemView.findViewById(R.id.stock_text_price_delta);
        ivFavouriteIcon = itemView.findViewById(R.id.stock_image_favourite_icon);
        layoutBasicInfo = itemView.findViewById(R.id.stock_layout_basic_info);
    }

    public void bindOn(Stock newItem) {
        clear();
        setTicker(newItem.getTicker());
        setCompanyInfo(newItem.getCompanyInfo());
        setPrice(newItem.getPrice());
        setFavourite(newItem.isFavourite());
    }

    private void clear() {
        setTicker(null);
        setCompanyInfo(new StockCompanyInfo());
        setPrice(new StockPrice());
        setFavourite(false);
        setOnFavouriteClicked(null);
    }

    public void setTicker(String ticker) {
        tvTicker.setText(ticker);
    }

    private void setCompanyInfo(StockCompanyInfo companyInfo) {
        setCompanyName(companyInfo.getCompanyName());
        setStockIcon(companyInfo.getLogoUrl());
        setCompanySiteUrl(companyInfo.getCompanySiteUrl());
    }

    public void setStockIcon(String iconUrl) {
        Glide.with(itemView)
                .load(iconUrl)
                .placeholder(R.drawable.outline_image_placeholder_52)
                .into(ivStockIcon);

    }

    public void setCompanyName(String companyName) {
        if (companyName != null) {
            String toDisplay = companyName.length() < 20
                    ? companyName
                    : companyName.substring(0, 20) + "...";
            tvCompanyName.setText(toDisplay);
        } else {
            tvCompanyName.setText("");
        }
    }

    private void setCompanySiteUrl(String companySiteUrl) {
        if (companySiteUrl != null && !companySiteUrl.trim().isEmpty()) {
            ivStockIcon.setOnClickListener(v -> openCompanySiteInBrowser(companySiteUrl));
        } else {
            ivStockIcon.setOnClickListener(null);
        }
    }

    private void openCompanySiteInBrowser(String companySiteUrl) {
        if (companySiteUrl != null && !companySiteUrl.trim().isEmpty()) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(companySiteUrl));
            itemView.getContext().startActivity(browserIntent);
        }
    }

    private void setPrice(StockPrice price) {
        setCurrPrice(price.getCurrPrice());
        setDeltaPrice(price.getDeltaPrice(),
                price.getDeltaPricePercent(),
                price.isDeltaPricePositive());
        setDeltaPositive(price.isDeltaPricePositive());
    }

    public void setCurrPrice(String currPrice) {
        if (currPrice != null) {
            tvCurrPrice.setText("$" + currPrice);
        } else {
            tvCurrPrice.setText("");
        }
    }

    public void setDeltaPrice(String deltaPrice, String deltaPricePercent, boolean deltaPositive) {
        if (deltaPrice != null && deltaPricePercent != null) {
            String toDisplay = deltaPositive ? "+" : "-";
            toDisplay += "$" + deltaPrice + " (" + deltaPricePercent.replace(".", ",") + "%)";
            tvDeltaPrice.setText(toDisplay);
        } else {
            tvDeltaPrice.setText("");
        }
    }

    public void setFavourite(boolean favourite) {
        Context context = itemView.getContext();
        Drawable icon;

        if (favourite) {
            icon = context.getDrawable(R.drawable.outline_star_24);
        } else {
            icon = context.getDrawable(R.drawable.outline_star_outline_24);
        }

        ivFavouriteIcon.setImageDrawable(icon);
    }

    public void setDeltaPositive(boolean deltaPositive) {
        int color;
        if (deltaPositive) {
            color = itemView.getContext().getColor(R.color.colorGreen);
        } else {
            color = itemView.getContext().getColor(R.color.colorRed);
        }

        tvDeltaPrice.setTextColor(color);
    }

    public void setOnFavouriteClicked(View.OnClickListener listener) {
        layoutBasicInfo.setOnClickListener(listener);
    }
}
