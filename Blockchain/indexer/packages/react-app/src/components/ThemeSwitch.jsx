import React, { useEffect, useState } from "react";
import { useThemeSwitcher } from "react-css-theme-switcher";
import { IMAGES_PATH } from "../constants";

export default function ThemeSwitcher({isDarkMode, setIsDarkMode}) {
  // const theme = window.localStorage.getItem("theme");
  // const [isDarkMode, setIsDarkMode] = useState(!(!theme || theme === "light"));
  const { switcher, currentTheme, themes } = useThemeSwitcher();

  useEffect(() => {
    window.localStorage.setItem("theme", currentTheme);
  }, [currentTheme]);

  const toggleTheme = () => {
    setIsDarkMode(currentTheme === "light");
    switcher({ theme: currentTheme === "light" ? themes.dark : themes.light });
  };

  return (
    <div className="main fade-in">
      <img style={{height: "20px"}} onClick={toggleTheme} src={currentTheme === "light" ? IMAGES_PATH + "/contrast.svg" : IMAGES_PATH + "/contrast-white.svg"}></img>
    </div>
  );
}
