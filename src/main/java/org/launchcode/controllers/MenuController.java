package org.launchcode.controllers;

import org.launchcode.models.Cheese;
import org.launchcode.models.Menu;
import org.launchcode.models.data.CheeseDao;
import org.launchcode.models.data.MenuDao;
import org.launchcode.models.form.AddMenuItemForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

/**
 * Created by azhar on 4/11/2017.
 */

@Controller
@RequestMapping(value = "menu")
public class MenuController {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private CheeseDao cheeseDao;

    @RequestMapping(value = "")
    public String index(Model model) {
        model.addAttribute("menus", menuDao.findAll());
        model.addAttribute("title", "All Menus");
        return "menu/index";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String addDisplay(Model model) {
        model.addAttribute("title", "All Menus");
        model.addAttribute(new Menu());
        return "menu/add";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public String addProcess(Model model, @ModelAttribute @Valid Menu menu, Errors errors) {

        if (errors.hasErrors()) {
            model.addAttribute("title", "Add Menu");
            return "menu/add";
        } else {
            menuDao.save(menu);
            //hibernate automatically saves new menu with a unique id
            return "redirect:view/"+menu.getId();
        }
    }

    @RequestMapping(value = "view/{menuID}")
    public String viewMenu(Model model, @PathVariable("menuID") int menuID) {
        Menu menu = menuDao.findOne(menuID);
        model.addAttribute("title", menu.getName());
        model.addAttribute("menu", menu);
        return "menu/view";
    }

    @RequestMapping(value = "add-item/{menuID}", method = RequestMethod.GET)
    public String addItem(Model model, @PathVariable("menuID") int menuID) {

        Menu menu = menuDao.findOne(menuID);
        AddMenuItemForm form = new AddMenuItemForm(menu, cheeseDao.findAll());

        model.addAttribute("form", form);
        model.addAttribute("title", "Add item to menu: " + menu.getName());
        return "menu/add-item";
    }

    @RequestMapping(value = "add-item", method = RequestMethod.POST)
    public String addItem(Model model, @ModelAttribute @Valid AddMenuItemForm form, Errors errors) {
        if (errors.hasErrors()) {
            model.addAttribute("form", form);
            return "menu/add-item";
        } else {

            Cheese theCheese = cheeseDao.findOne(form.getCheeseId());

            Menu theMenu = menuDao.findOne(form.getMenuId());
            theMenu.addItem(theCheese);
            menuDao.save(theMenu);

            return "redirect:/menu/view/" + theMenu.getId();
        }


    }

}
