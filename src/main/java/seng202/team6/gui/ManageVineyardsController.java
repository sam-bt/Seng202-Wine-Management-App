package seng202.team6.gui;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import seng202.team6.gui.controls.card.Card;
import seng202.team6.gui.controls.cardcontent.VineyardCardContent;
import seng202.team6.gui.popup.GeneralPopupController;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.Vineyard;
import seng202.team6.service.VineyardService;
import seng202.team6.util.ImageReader;

public class ManageVineyardsController extends Controller {
  private final ObservableMap<Vineyard, Card> vineyardCards = FXCollections.observableHashMap();
  private final VineyardService vineyardService;

  @FXML
  TilePane vineyardsViewContainer;

  /**
   * Constructs a new ManageVineyardsController.
   *
   * @param managerContext the manager context
   */
  public ManageVineyardsController(ManagerContext managerContext) {
    super(managerContext);
    this.vineyardService = new VineyardService(managerContext.getDatabaseManager());
    bindToVineyardService();
  }

  @Override
  public void init() {
    vineyardService.init();
  }

  @FXML
  void onCreateClick() {
    openCreateModifyPopup(null);
  }

  private void bindToVineyardService() {
    ObservableList<Vineyard> vineyards = vineyardService.get();
    vineyards.addListener((ListChangeListener<Vineyard>) change -> {
      while (change.next()) {
        if (change.wasAdded()) {
          change.getAddedSubList().forEach(vineyard -> {
            Card card = createVineyardCard(vineyard);
            vineyardCards.put(vineyard, card);
            vineyardsViewContainer.getChildren().add(card);
          });
        }
        if (change.wasRemoved()) {
          change.getRemoved().forEach(vineyard -> {
            Card card = vineyardCards.get(vineyard);
            vineyardsViewContainer.getChildren().remove(card);
            vineyardCards.remove(vineyard);
          });
        }
      }
    });
  }

  private Card createVineyardCard(Vineyard vineyard) {
    Card card = new Card(vineyardsViewContainer.widthProperty(),
        vineyardsViewContainer.hgapProperty());
    VineyardCardContent cardContent = new VineyardCardContent(vineyard, 200, 150);
    card.setOnMouseClicked(event -> openCreateModifyPopup(vineyard));
    card.setAlignment(Pos.CENTER);
    card.getChildren().add(cardContent);
    return card;
  }

  private void openCreateModifyPopup(Vineyard vineyard) {
    GeneralPopupController popup = getManagerContext().getGuiManager().showPopup();
    popup.setTitle((vineyard == null ? "Create" : "Modify" + " Vineyard"));

    final Label nameLabel = new Label("Name");
    final TextField nameTextField = new TextField(vineyard == null ? "" : vineyard.getName());
    final Label addressLabel = new Label("Address");
    final TextField addressTextField = new TextField(vineyard == null ? "" : vineyard.getAddress());
    final Label regionLabel = new Label("Region");
    final TextField regionTextField = new TextField(vineyard == null ? "" : vineyard.getRegion());
    final Label websiteLabel = new Label("Website");
    final TextField websiteTextField = new TextField(vineyard == null ? "" : vineyard.getWebsite());
    final Label logoUrlLabel = new Label("Logo URL");
    final TextField logoUrlTextField = new TextField(vineyard == null ? "" : vineyard.getLogoUrl());
    final Label descriptionLabel = new Label("Description");
    final TextArea descriptionTextArea = new TextArea(vineyard == null ? "" :
        vineyard.getDescription());
    descriptionTextArea.setWrapText(true);

    VBox wrapper = new VBox();
    wrapper.setSpacing(10);
    wrapper.getChildren().addAll(nameLabel, nameTextField, addressLabel, addressTextField,
        regionLabel, regionTextField, websiteLabel, websiteTextField, logoUrlLabel,
        logoUrlTextField, descriptionLabel, descriptionTextArea);
    popup.addContent(wrapper);

    if (vineyard != null) {
      popup.addButton("Modify", () -> modifyVineyard(popup, vineyard, nameTextField.getText(),
          addressTextField.getText(), regionTextField.getText(), websiteTextField.getText(),
          logoUrlTextField.getText(), descriptionTextArea.getText()));
      popup.addButton("Delete", () -> deleteVineyard(popup, vineyard));
      return;
    }
    popup.addButton("Create", () -> createVineyard(popup, nameTextField.getText(),
        addressTextField.getText(), regionTextField.getText(), logoUrlTextField.getText(),
        descriptionTextArea.getText()));
  }

  private void createVineyard(GeneralPopupController popup, String name, String address,
      String region, String logoUrl, String description) {
    if (!validateFields(popup, null, name, address, region, logoUrl)) {
      return;
    }
    popup.close();
  }

  private void modifyVineyard(GeneralPopupController popup, Vineyard vineyard, String name, String address,
      String region, String website, String logoUrl, String description) {
    if (!validateFields(popup, vineyard, name, address, region, logoUrl)) {
      return;
    }
    vineyard.setName(name);
    vineyard.setAddress(address);
    vineyard.setRegion(region);
    vineyard.setWebsite(website);
    vineyard.setLogoUrl(logoUrl);
    vineyard.setDescription(description);
    popup.close();
  }

  private void deleteVineyard(GeneralPopupController popup, Vineyard vineyard) {
    vineyardService.delete(vineyard);
    popup.close();
  }

  private boolean validateFields(GeneralPopupController popup, Vineyard modifiyingVineyard,
      String name, String address, String region, String logoUrl) {
    if (name.length() < 3 || name.length() > 64) {
      popup.setErrorMessage("The vineyard name must be between 3 and 64 characters.");
      return false;
    }
    if (address.length() < 3 || address.length() > 64) {
      popup.setErrorMessage("The vineyard address must be between 3 and 64 characters.");
      return false;
    }
    if (region.length() < 3 || region.length() > 64) {
      popup.setErrorMessage("The vineyard region must be between 3 and 64 characters.");
      return false;
    }
    if (vineyardService.get().stream().filter(vineyard -> vineyard != modifiyingVineyard)
        .anyMatch(vineyard -> vineyard.getName().equalsIgnoreCase(name))) {
      popup.setErrorMessage("A vineyard with that name already exists");
      return false;
    }
    if (!validateLogoUrl(logoUrl)) {
      popup.setErrorMessage("The logo URL was invalid and did not point to an image");
      System.out.println(logoUrl);
      return false;
    }
    return true;
  }

  private boolean validateLogoUrl(String imageUrl) {
    try {
      ImageReader.loadImageFromUrl(imageUrl);
      return true;
    } catch (IllegalArgumentException error) {
      return false;
    }
  }
}
