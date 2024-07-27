import React from "react";
import { UseFormRegister } from "react-hook-form";

interface IFormValues {}

const Select = React.forwardRef<
  HTMLSelectElement,
  { options: string[] } & ReturnType<UseFormRegister<IFormValues>>
>(({ onChange, onBlur, name, options }, ref) => (
  <div className="form-group">
    <select
      name={name}
      ref={ref}
      onChange={onChange}
      onBlur={onBlur}
      className="form-select"
    >
      {options.map((item: string) => {
        return (
          <option key={item} value={item.toLowerCase()}>
            {item}
          </option>
        );
      })}
    </select>
  </div>
));
export default Select;
