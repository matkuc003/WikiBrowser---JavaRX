
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import io.reactivex.rxjavafx.sources.Change;
import io.reactivex.schedulers.Schedulers;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Controller {

    @FXML
    ComboBox<String> comboBox;
    @FXML
    TextField textField;
    @FXML
    public void initialize() {

/*        JavaFxObservable.eventsOf(textField, KeyEvent.KEY_RELEASED)
                .map(keyEvent -> textField.getText())
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribe(s -> {
                    Platform.runLater(() -> {
                        comboBox.getItems().addAll(FXCollections.observableList(httpReq(s)));
                    });
                });*/

        JavaFxObservable.changesOf(textField.textProperty())
                .map(s -> {
                    Platform.runLater(() -> {
                        comboBox.getItems().clear();
                    });
                    return s;
                })
                .map(Change::getNewVal)
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribe(s -> {
                    Platform.runLater(() -> {
                        comboBox.getItems().addAll(FXCollections.observableList(httpReq(s)));
                    });
                });
        JavaFxObservable.valuesOf(comboBox.valueProperty()).subscribe(s -> {
            Platform.runLater(() -> {
                textField.setText(s);
                String openTitle = s.replace(" ", "%20");

                try {
                    Runtime.getRuntime().exec(new String[]{"cmd", "/c", "start chrome https://pl.wikipedia.org/wiki/" + openTitle});
                } catch (IOException e) {
                    e.printStackTrace();
                }



            });
        });


    }


    public List<String> httpReq(String keyWord) {
        List<String> list = new ArrayList<>();
        Connection connect = Jsoup.connect("https://pl.wikipedia.org/w/api.php?action=query")
                .data("format", "xml")
                .data("titles", keyWord)
                .data("prop", "links")
                .data("pllimit", "500");
        Document document = null;
        try {
            document = connect.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements allH1 = document.getElementsByAttribute("title");
        for (Element elem : allH1) {
            list.add(elem.attr("title"));
        }
        return list;
    }
}
