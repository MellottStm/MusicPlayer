package com.smt.UI;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

public class SearchView {

    @FXML
    private TextField searchField;   // 对应 fxml 里的 fx:id（建议加上）

    @FXML
    private Button searchButton;

    @FXML
    private ListView searchList;

    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (!keyword.isEmpty()) {
            System.out.println("搜索的内容:" + keyword);
            // 后面可以在这里写搜索逻辑
        } else {
            System.out.println("搜索内容为空!");
        }
    }

}
