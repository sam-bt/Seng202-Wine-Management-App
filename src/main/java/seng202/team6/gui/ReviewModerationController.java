package seng202.team6.gui;

import seng202.team6.managers.DatabaseManager;
import seng202.team6.managers.ManagerContext;

public class ReviewModerationController extends Controller{

    ManagerContext managerContext;

    DatabaseManager databaseManager;


    public ReviewModerationController(ManagerContext managerContext) {
        super(managerContext);
        this.managerContext = managerContext;
        this.databaseManager = managerContext.databaseManager;
    }




}
