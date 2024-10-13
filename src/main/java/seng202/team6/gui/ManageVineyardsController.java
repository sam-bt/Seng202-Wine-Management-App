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
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import seng202.team6.gui.controls.card.Card;
import seng202.team6.gui.controls.cardcontent.VineyardCardContent;
import seng202.team6.gui.popup.GeneralPopupController;
import seng202.team6.managers.ManagerContext;
import seng202.team6.model.GeoLocation;
import seng202.team6.model.Vineyard;
import seng202.team6.service.VineyardService;
import seng202.team6.util.GeolocationResolver;
import seng202.team6.util.ImageReader;

/**
 * Controller responsible for managing vineyards in the GUI.
 */
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

  /**
   * Initializes the controller, setting up the vineyard service and any required bindings.
   */
  @Override
  public void init() {
    vineyardService.init();
  }

  /**
   * Opens a popup window for creating a new vineyard.
   */
  @FXML
  void onCreateClick() {
    openCreateModifyPopup(null);
  }

  /**
   * Binds the vineyard service to the view, ensuring that the list of vineyards is kept
   * in sync with the user interface.
   */
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

  /**
   * Creates a new vineyard card and sets up its contents and event handlers.
   *
   * @param vineyard the vineyard entity to display in the card
   * @return the constructed Card for the vineyard
   */
  private Card createVineyardCard(Vineyard vineyard) {
    Card card = new Card(vineyardsViewContainer.widthProperty(),
        vineyardsViewContainer.hgapProperty());
    VineyardCardContent cardContent = new VineyardCardContent(vineyard, 200, 150);
    card.setOnMouseClicked(event -> openCreateModifyPopup(vineyard));
    card.setAlignment(Pos.CENTER);
    card.getChildren().add(cardContent);
    return card;
  }

  /**
   * Opens a popup for creating or modifying a vineyard.
   *
   * @param vineyard the vineyard to modify, or null if creating a new vineyard
   */
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
        addressTextField.getText(), regionTextField.getText(), websiteTextField.getText(),
        logoUrlTextField.getText(), descriptionTextArea.getText()));
  }

  /**
   * Creates a new vineyard using the provided input fields.
   *
   * @param popup the popup controller
   * @param name the name of the vineyard
   * @param address the address of the vineyard
   * @param region the region of the vineyard
   * @param website the website URL of the vineyard
   * @param logoUrl the URL of the vineyard's logo
   * @param description a description of the vineyard
   */
  private void createVineyard(GeneralPopupController popup, String name, String address,
      String region, String website, String logoUrl, String description) {
    if (!validateFields(popup, null, name, address, region, logoUrl)) {
      return;
    }
    getManagerContext().getGuiManager().showLoadingIndicator(() -> {
      GeoLocation geoLocation = validateLogoUrlAndGeolocation(popup, address, logoUrl);
      if (geoLocation == null) {
        return;
      }
      vineyardService.create(name, address, region, website, logoUrl, description, geoLocation);
    });
    popup.close();
  }

  /**
   * Modifies an existing vineyard with new data.
   *
   * @param popup the popup controller
   * @param vineyard the vineyard to modify
   * @param name the new name of the vineyard
   * @param address the new address of the vineyard
   * @param region the new region of the vineyard
   * @param website the new website of the vineyard
   * @param logoUrl the new logo URL of the vineyard
   * @param description the new description of the vineyard
   */
  private void modifyVineyard(GeneralPopupController popup, Vineyard vineyard, String name,
      String address, String region, String website, String logoUrl, String description) {
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

  /**
   * Deletes the specified vineyard.
   *
   * @param popup the popup controller
   * @param vineyard the vineyard to delete
   */
  private void deleteVineyard(GeneralPopupController popup, Vineyard vineyard) {
    vineyardService.delete(vineyard);
    popup.close();
  }

  /**
   * Validates the input fields for vineyard creation or modification.
   *
   * @param popup the popup controller
   * @param modifiyingVineyard the vineyard being modified, or null if creating
   * @param name the vineyard name
   * @param address the vineyard address
   * @param region the vineyard region
   * @param logoUrl the vineyard logo URL
   * @return {@code true} if the fields are valid, false otherwise
   */
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
      return false;
    }
    return true;
  }

  /**
   * Validates both the logo URL and the geolocation of the vineyard.
   *
   * @param popup the popup controller
   * @param address the vineyard address
   * @param logoUrl the vineyard logo URL
   * @return the geolocation of the vineyard, or null if validation failed
   */
  private GeoLocation validateLogoUrlAndGeolocation(GeneralPopupController popup, String address,
      String logoUrl) {
    if (!validateLogoUrl(logoUrl)) {
      popup.setErrorMessage("The logo URL was invalid and did not point to an image");
      return null;
    }

    GeolocationResolver geolocationResolver = new GeolocationResolver();
    try {
      return geolocationResolver.resolveLocation(address).join();
    } catch (Exception error) {
      popup.setErrorMessage("The address was invalid and could not be resolved.");
      return null;
    }
  }

  /**
   * Validates the vineyard logo URL by attempting to load the image.
   *
   * @param imageUrl the URL of the logo
   * @return true if the URL is valid and points to an image, false otherwise
   */
  private boolean validateLogoUrl(String imageUrl) {
    try {
      ImageReader.loadImageFromUrl(imageUrl);
      return true;
    } catch (IllegalArgumentException error) {
      return false;
    }
  }
}
