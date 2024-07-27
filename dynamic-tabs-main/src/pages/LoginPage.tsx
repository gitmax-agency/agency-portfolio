import React from "react";
import { LogoIcon } from "../assets/icons";
import Form from "../components/Forms/Form";
import * as yup from "yup";
import { useDispatch, useSelector } from "react-redux";
import { loginAction } from "../store/user/user";
import { RootState } from "../store/configureStore";

const schema = yup.object().shape({
  username: yup.string().required(),
  password: yup.string().required(),
});
const Languages = [
  "English",
  "Spanish",
  "French",
  "Chinese",
  "Hindi",
  "Korean",
];
const Locations = [
  "Singapore",
  "United States",
  "Malaysia",
  "China",
  "Korea",
  "India",
];
const LoginPage = () => {
  const dispatch = useDispatch();
  const handleSubmit = (data) => {
    dispatch(loginAction({ ...data }));
  };
  const user = useSelector((state: RootState) => state.user);
  return (
    <div className="login">
      <LogoIcon />

      <Form
        name="Login"
        schema={schema}
        onSubmit={handleSubmit}
        inputs={[
          {
            name: "username",
            placeholder: "Email",
          },
          {
            name: "password",
            placeholder: "Password",
            type: "password",
          },
        ]}
        // checkbox={["Remember me"]}
        // selects={[
        //   {
        //     name: "languages",
        //     options: Languages,
        //   },
        //   {
        //     name: "locations",
        //     options: Locations,
        //   },
        // ]}
        mode="onSubmit"
      />
      {user.error && <p className="text--danger">{user.error}</p>}
    </div>
  );
};

export default LoginPage;
