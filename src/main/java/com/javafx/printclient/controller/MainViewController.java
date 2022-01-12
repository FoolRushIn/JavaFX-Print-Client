package com.javafx.printclient.controller;


import com.javafx.printclient.service.MainService;
import com.javafx.printclient.utils.LoadUtil;
import com.jfoenix.controls.JFXTabPane;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class MainViewController extends BaseController implements Initializable {
    @FXML
    public SplitPane splitPane;

    public ObservableList<Node> centerPaneList;

    @FXML
    public StackPane stackPane;

    @FXML
    BorderPane mainPane;

    @FXML
    JFXTabPane centerTabPane;

    @FXML
    VBox vBoxToDrag;

    @Autowired
    MainService mainService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initView();
    }



    //加载中间界面的多个模块
    private void initView() {
        Tab tab0 = new Tab("打印报表进程", LoadUtil.loadFXML("fxml/items/print_porcess_view.fxml"));
        Tab tab1 = new Tab("打印机管理", LoadUtil.loadFXML("fxml/items/printer_management_view.fxml"));
        Tab tab2 = new Tab("模板配置管理", LoadUtil.loadFXML("fxml/items/printer_management_view.fxml"));
        Tab tab3 = new Tab("映射关系管理", LoadUtil.loadFXML("fxml/items/printer_management_view.fxml"));
        centerTabPane.getTabs().addAll(tab0);
        centerTabPane.getTabs().addAll(tab1);
        centerTabPane.getTabs().addAll(tab2);
        centerTabPane.getTabs().addAll(tab3);
        centerPaneList = splitPane.getItems();
    }

    /**
     * @param data
     * @param flag
     * @return void
     * @Author heziyuan
     * @Description //展示搜索音乐的结果,并更新界面
     **/
//    @Override
//    public void showResult(Object data, int flag) {
//        String keyword = (String) data;
//        if (keyword != null && !keyword.equals("")) {
//            Map<String, Object> map = iMusicService.getSearchMusicHash(tfSearchKeyword.getText().trim(), 1, 30);
//            List<String> hashs = (List<String>) map.get("list");
//            int total = (int) map.get("total");
//            //封装表格顶部信息
//            TableViewPlayInfoBean infoBean = new TableViewPlayInfoBean(CommonResources.KUGOU_ICON, "搜索" + keyword, String.valueOf(total), null);
//            //显示
//            ((MusicTvController) BaseController.BC_CONTEXT.get(MusicTvController.class.getName())).setPlayData(infoBean);
//
//            //封装歌曲list
//            List<KuGouMusicPlay> playList = iMusicService.handleHashs(hashs);
//            List<KuGouMusicPlay.DataBean> beanList = new ArrayList<>();
//            playList.forEach(playItem -> beanList.add(playItem.getData()));
//            BaseController.BC_CONTEXT.get(MusicTvController.class.getName()).showResult(beanList, 1);
//            Platform.runLater(() -> BaseController.BC_CONTEXT.get(MusicTvController.class.getName()).initData(total));
//        }
//    }
}