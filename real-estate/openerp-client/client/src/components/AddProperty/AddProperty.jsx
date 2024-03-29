import {Container, Modal, Stepper} from "@mantine/core";
import {useState} from "react";
import AddLocation from "../AddLocation/AddLocation";
import {useAuth0} from "@auth0/auth0-react";
import UploadImage from "../UploadImage/UploadImage";
import BasicDetail from "../BasicDetail/BasicDetail";
import Facilities from "../Facilities/Facilities";

const AddProperty = ({opened, setOpened}) => {
    const [active, setActive] = useState(0);
    // const {user} = useAuth0();

    const [propertyDetails, setPropertyDetails] = useState({
        position:[21,105],
        province: "",
        district: "",
        address: "",

        imageUrls: [],

        title: "",
        description: "",
        price: 0,


        acreage: 0,
        bedroom: 0,
        parking: 0,
        bathroom: 0,
        floor: 1,

        // userEmail: user?.email,
    });

    const nextStep = () => {
        setActive((current) => (current < 4 ? current + 1 : current));
    };

    const prevStep = () => {
        setActive((current) => (current > 0 ? current - 1 : current));
    };

    return (
        <Modal
            opened={true}
            onClose={() => setOpened(false)}
            closeOnClickOutside
            size={"90rem"}
        >
            <Container h={"40rem"} w={"100%"}>
                <Stepper
                    active={active}
                    onStepClick={setActive}
                    breakpoint="sm"
                    allowNextStepsSelect={false}
                >
                    <Stepper.Step label="Location" description="Address">
                        <AddLocation
                            nextStep={nextStep}
                            propertyDetails={propertyDetails}
                            setPropertyDetails={setPropertyDetails}
                        />
                    </Stepper.Step>
                    <Stepper.Step label="Images" description="Upload ">
                        <UploadImage
                            prevStep={prevStep}
                            nextStep={nextStep}
                            propertyDetails={propertyDetails}
                            setPropertyDetails={setPropertyDetails}
                        />
                    </Stepper.Step>
                    <Stepper.Step label="Basics" description="Details">
                        <BasicDetail
                            prevStep={prevStep}
                            nextStep={nextStep}
                            propertyDetails={propertyDetails}
                            setPropertyDetails={setPropertyDetails}
                        />
                    </Stepper.Step>
                    <Stepper.Step>
                        <Facilities
                            prevStep={prevStep}
                            propertyDetails={propertyDetails}
                            setPropertyDetails={setPropertyDetails}
                            setOpened={setOpened}
                            setActiveStep={setActive}
                        />
                    </Stepper.Step>
                    <Stepper.Completed>
                        Completed, click back button to get to previous step
                    </Stepper.Completed>
                </Stepper>
            </Container>
        </Modal>
    );
};

export default AddProperty;
