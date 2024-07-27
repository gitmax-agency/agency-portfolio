import React from "react";
import { UseFormRegister } from "react-hook-form";
interface InputProps {
  label: string;
  register: UseFormRegister<any>;
}
const Checkbox = ({ label, register }: InputProps) => (
  <label className="form-checkbox">
    {label}
    <input
      type="checkbox"
      className="input__checkbox"
      {...register(label.split(" ").join("").toLowerCase())}
    />
  </label>
);

export default Checkbox;
