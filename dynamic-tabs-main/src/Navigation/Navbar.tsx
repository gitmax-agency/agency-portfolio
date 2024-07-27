import React from "react";
import Favorites from "./NavbarComponents/Favorites";
import Menu from "./Menu/Menu";
import Notifications from "./NavbarComponents/Notifications";
import ProjectManagement from "./NavbarComponents/ProjectManagement";
import Help from "./NavbarComponents/Help";
import ModuleNavItems from "./ModuleNavComponents/ModuleNavItems";
import Tabs from "./Tabs/Tabs";

const Navbar = () => {
  return (
    <div className="navbar">
      <Menu />
      <Favorites />
      <Notifications />
      <ProjectManagement />
      <Help />
      <Tabs />
      <ModuleNavItems />
    </div>
  );
};

export default Navbar;
