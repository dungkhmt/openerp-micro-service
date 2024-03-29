import { useAuth0 } from "@auth0/auth0-react";
import { Box, Button, Group, NumberInput } from "@mantine/core";
import { useForm } from "@mantine/form";
import React, { useContext } from "react";
import PostRequest from "../../services/PostRequest";
import {apiAddPostSell} from "../../services/AppRequest";

const Facilities = ({
  prevStep,
  propertyDetails,
  setPropertyDetails,
  setOpened,
  setActiveStep,
}) => {
  const form = useForm({
    initialValues: {
      acreage: propertyDetails.acreage,
      bedroom: propertyDetails.bedroom,
      parking: propertyDetails.parking,
      bathroom: propertyDetails.bathroom,
      floor: propertyDetails.floor,
    },
    validate: {
      bedrooms: (value) => (value < 1 ? "Must have atleast one room" : null),
      bathrooms: (value) =>
        value < 1 ? "Must have atleast one bathroom" : null,
    },
  });

  const { acreage, bedroom, parking, bathroom, floor } = form.values;

  const handleSubmit = () => {
    const { hasErrors } = form.validate();
    if (!hasErrors) {
      setPropertyDetails((prev) => ({
        ...prev, acreage, bedroom, parking, bathroom, floor
      }));
      console.log("data gui", propertyDetails)
      apiAddPostSell(propertyDetails)
      // const postRequest = new PostRequest();
      //
      // postRequest.addPostSell(propertyDetails);
      // mutate();
    }
  };

  // ==================== upload logic
  // const { user } = useAuth0();
  // const {
  //   userDetails: { token },
  // } = useContext(UserDetailContext);
  // const { refetch: refetchProperties } = useProperties();
  //
  // const {mutate, isLoading} = useMutation({
  //   mutationFn: ()=> createResidency({
  //       ...propertyDetails, facilities: {bedrooms, parkings , bathrooms},
  //   }, token),
  //   onError: ({ response }) => toast.error(response.data.message, {position: "bottom-right"}),
  //   onSettled: ()=> {
  //     toast.success("Added Successfully", {position: "bottom-right"});
  //     setPropertyDetails({
  //       title: "",
  //       description: "",
  //       price: 0,
  //       country: "",
  //       city: "",
  //       address: "",
  //       image: null,
  //       facilities: {
  //         bedrooms: 0,
  //         parkings: 0,
  //         bathrooms: 0,
  //       },
  //       userEmail: user?.email,
  //     })
  //     setOpened(false)
  //     setActiveStep(0)
  //     refetchProperties()
  //   }
  //
  // })

  return (
    <Box maw="30%" mx="auto" my="sm">
      <form
        onSubmit={(e) => {
          e.preventDefault();
          handleSubmit();
        }}
      >
        <NumberInput
            withAsterisk
            label="No of acreage"
            min={0}
            {...form.getInputProps("acreage")}
        />
        <NumberInput
          withAsterisk
          label="No of Bedroom"
          min={0}
          {...form.getInputProps("bedroom")}
        />
        <NumberInput
          label="No of Parking"
          min={0}
          {...form.getInputProps("parking")}
        />
        <NumberInput
          withAsterisk
          label="No of Bathroom"
          min={0}
          {...form.getInputProps("bathroom")}
        />
        <NumberInput
            withAsterisk
            label="No of floor"
            min={1}
            {...form.getInputProps("floor")}
        />
        <Group position="center" mt="xl">
          <Button variant="default" onClick={prevStep}>
            Back
          </Button>
          <Button type="submit" color="green">
            Add Property
          </Button>
        </Group>
      </form>
    </Box>
  );
};

export default Facilities;
