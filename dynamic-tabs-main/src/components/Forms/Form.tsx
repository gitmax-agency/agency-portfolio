import React from "react";
//style
import "./style.scss";
//components
import Button from "../Buttons/Button";
import Input from "./Input";
import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import { AnyObjectSchema } from "yup";
import Select from "./Select";
import Checkbox from "./Checkbox";

interface Props {
  name: string;
  schema?: AnyObjectSchema;
  mode?: "onChange" | "onBlur" | "onSubmit" | "onTouched" | "all";
  onSubmit: (data: any) => void;
  inputs?: IInput[];
  selects?: ISelect[];
  checkbox?: string[];
}

export interface IInput {
  name: string;
  placeholder?: string;
  type?: string;
}
export interface ISelect {
  name: string;
  options: string[];
}

const Form: React.FC<Props> = ({
  schema,
  onSubmit,
  inputs,
  checkbox,
  selects,
  mode,
  name,
}) => {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(schema),
    mode: mode,
  });

  return (
    <form className="form" onSubmit={handleSubmit(onSubmit)}>
      <h3>{name}</h3>
      {inputs && (
        <div className="form__inputs">
          {inputs.map((input) => {
            return (
              <Input
                key={input.name}
                input={input}
                error={errors[input.name]?.message}
                register={register}
              />
            );
          })}
        </div>
      )}
      {checkbox &&
        checkbox.map((item) => (
          <Checkbox key={item} label={item} register={register} />
        ))}
      {selects && (
        <div className="form__selects">
          {selects.map((select) => {
            return (
              <Select
                key={select.name}
                options={select.options}
                {...register(select.name)}
              />
            );
          })}
        </div>
      )}

      <Button onClick={handleSubmit(onSubmit)}>{name}</Button>
    </form>
  );
};
export default Form;
