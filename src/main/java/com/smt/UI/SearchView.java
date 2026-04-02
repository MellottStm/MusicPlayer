package com.smt.UI;

import com.alibaba.fastjson.JSONObject;
import com.smt.Data.MusicItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

public class SearchView {

    @FXML
    private TextField searchField;   // 对应 fxml 里的 fx:id（建议加上）

    @FXML
    private Button searchButton;

    @FXML
    private ProgressIndicator loadingIndicator;

    @FXML
    public ListView<MusicItem> searchList;


    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            System.out.println("搜索的内容:" + keyword);
            // 后面可以在这里写搜索逻辑
            showSearchResult(null);
        } else {
            System.out.println("搜索内容为空!");
        }
    }


    public void showSearchResult (JSONObject resJson) {
        loadingIndicator.setVisible(false);
        searchList.setVisible(true);
        searchList.setCellFactory(listview -> new MusicCell());
        ObservableList<MusicItem> items = FXCollections.observableArrayList(
                new MusicItem("1", "game", "滨崎步", null,null),
                new MusicItem("2", "boy & girl", "滨崎步", null,null)
        );
        searchList.setItems(items);
    }


}
