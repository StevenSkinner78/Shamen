package gov.doc.isu.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;

import org.apache.log4j.Logger;

import net.sf.navigator.displayer.TabbedMenuDisplayer;
import net.sf.navigator.menu.MenuComponent;

/**
 * ShamenTabbedMenuDisplayer is the Struts Menu class for Shamen specific things in Shamen Web.
 *
 * @see net.sf.navigator.displayer.TabbedMenuDisplayer
 * @author sls000is
 */
public class ShamenTabbedMenuDisplayer extends TabbedMenuDisplayer {

    private Logger log = Logger.getLogger("gov.doc.isu.taglib.ShamenTabbedMenuDisplayer");

    /**
     * {@inheritDoc}
     */
    public void display(MenuComponent menu) throws JspException, IOException {
        log.debug("Entering display");
        log.debug("Entry parameters are: menu=" + String.valueOf(menu));
        if(isAllowed(menu)){
            displayComponents(menu, 0);
        }// end if
        log.debug("Exiting display");
    }// end display

    /**
     * {@inheritDoc}
     */
    protected void displayComponents(MenuComponent menu, int level) throws JspException, IOException {
        log.debug("Entering displayComponents");
        log.debug("Entry parameters are: menu=" + String.valueOf(menu) + ", level=" + String.valueOf(level));
        MenuComponent[] components = menu.getMenuComponents();

        if(components.length > 0){
            this.out.print("\t<li class=\"nav-item \">");

            String menuClass = "submenu";

            if(level >= 1){
                menuClass = "deepmenu";
            }// end if

            if(menu.getUrl() == null){
                log.debug("The Menu '" + getMessage(menu.getTitle()) + "' does not have a location defined, using first submenu's location");

                menu.setUrl(components[0].getUrl());
            }// end if

            this.out.print(this.displayStrings.getMessage("tmd.menu.tab", menu.getUrl(), super.getMenuToolTip(menu), getExtra(menu), getMessage(menu.getTitle())));

            for(int i = 0;i < components.length;i++){
                if(isAllowed(components[i])){
                    if(components[i].getMenuComponents().length > 0){
                        if(menuClass.equals("submenu")){
                            this.out.print("<li class=\"nav-item \">");
                        }// end if

                        displayComponents(components[i], level + 1);

                        this.out.println("</ul></li>");

                        if(i == components[i].getMenuComponents().length - 1){
                            this.out.println("</li>");
                        }// end if
                    }else{
                        this.out.println(this.displayStrings.getMessage("tmd.menu.item", components[i].getUrl(), super.getMenuToolTip(components[i]), super.getExtra(components[i]), getMessage(components[i].getTitle())));
                    }// end if/else
                }// end if
            }// end for

            if(menuClass.equals("submenu")){
                this.out.println("\t</ul>");
            }// end if

            this.out.print("\t</li>");
        }else{
            this.out.println(this.displayStrings.getMessage("tmd.menu.item", menu.getUrl(), super.getMenuToolTip(menu), super.getExtra(menu), getMessage(menu.getTitle())));
        }// end if/else
        log.debug("Exiting displayComponents");
    }// end displayComponents
}// end class
