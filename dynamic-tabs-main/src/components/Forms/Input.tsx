import React from "react";
import { UseFormRegister } from "react-hook-form";
import { IInput } from "./Form";

interface InputProps {
  error: string;
  input: IInput;
  register: UseFormRegister<any>;
}
const Input = ({ error, input, register }: InputProps) => (
  <div className="form-group">
    <input
      placeholder={input.placeholder}
      type={input.type || "text"}
      className={`input input--primary ${error && "input--danger"}`}
      {...register(input.name)}
    />
    <p className="text--danger">{error}</p>
  </div>
);
export default Input;
