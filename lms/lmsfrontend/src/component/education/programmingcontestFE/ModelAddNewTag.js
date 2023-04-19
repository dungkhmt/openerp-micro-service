import HustModal from "component/common/HustModal";
import React, {useState} from "react";
import {TextField} from "@mui/material";
import {useTranslation} from "react-i18next";
import {addNewTag} from "./service/TagService";

const ModelAddNewTag = ({isOpen, handleSuccess, handleClose}) => {

  const {t} = useTranslation(
    ["education/programmingcontest/problem", "common", "validation"]
  );

  const [tagName, setTagName] = useState("");
  const [tagDescription, setTagDescription] = useState("");

  const [loading, setLoading] = useState(false);

  const handleSubmit = () => {
    let body = {
      name: tagName,
      description: tagDescription,
    };

    setLoading(true);

    addNewTag(
      body,
      handleSuccess,
      () => {
        setLoading(false);
        handleClose();
      },
    )
  }

  return (
    <HustModal
      open={isOpen}
      onOk={handleSubmit}
      textOk={t("common:save")}
      onClose={handleClose}
      isLoading={loading}
      title={t("common:addNew")}
    >
      <TextField
        fullWidth
        required
        label={"Tag"}
        placeholder="Tag"
        value={tagName}
        onChange={(event) => {
          setTagName(event.target.value);
        }}
      />
      <TextField
        fullWidth
        label={t("common:description")}
        placeholder={t("common:description")}
        value={tagDescription}
        onChange={(event) => {
          setTagDescription(event.target.value);
        }}
        sx={{marginTop: "16px"}}
      />
    </HustModal>
  );
}

export default React.memo(ModelAddNewTag);