package gui.actions;

public class ActionManager {

    private final EditAction editAction;
    private final ViewAction viewAction;
    private final RunAction runAction;
    private final PrettyAction prettyAction;

    public ActionManager(){
    editAction = new EditAction();
    viewAction = new ViewAction();
    runAction = new RunAction();
    prettyAction = new PrettyAction();
    }

    public ViewAction getViewAction() {
        return viewAction;
    }

    public EditAction getEditAction() {
        return editAction;
    }

    public RunAction getRunAction() {
        return runAction;
    }

    public PrettyAction getPrettyAction() {
        return prettyAction;
    }
}
