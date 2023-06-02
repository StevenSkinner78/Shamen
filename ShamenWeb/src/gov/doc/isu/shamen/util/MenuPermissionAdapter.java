package gov.doc.isu.shamen.util;

import java.util.ArrayList;

import net.sf.navigator.menu.MenuComponent;
import net.sf.navigator.menu.PermissionsAdapter;

/**
 * The permissions adapter for the tabs that will display based on authority and what level of the process in the application.
 * 
 * @author Steven Skinner
 */
public class MenuPermissionAdapter implements PermissionsAdapter {
    private ArrayList<String> menuNames;

    /**
     * Creates a new instance of SimplePermissionAdapter
     * 
     * @param theMenuNames
     *        the name of the menu items
     */
    public MenuPermissionAdapter(String[] theMenuNames) {
        menuNames = new ArrayList<String>();

        if(theMenuNames != null){
            for(int i = 0;i < theMenuNames.length;i++){
                menuNames.add(theMenuNames[i]);
            }
        }

    }

    /**
     * If the menu is allowed, this should return true.
     * 
     * @param menu
     *        the MenuComponent to check
     * @return whether or not the menu is allowed.
     */
    public boolean isAllowed(MenuComponent menu) {
        return menuNames.contains(menu.getName());
    }
}
