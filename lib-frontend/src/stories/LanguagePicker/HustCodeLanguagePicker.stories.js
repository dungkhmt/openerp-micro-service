import React, { useState } from 'react';
import {HustCodeLanguagePicker} from "../../components";
export default {
    title: 'Components/HustCodeLanguagePicker',
    component: HustCodeLanguagePicker,
    tags: ['autodocs']
};

const Template = args => {
    const [language, setLanguage] = useState("CPP");
    const handleChange = e => {
        setLanguage(e.target.value);
        args.onChangeLanguage(e);
    };

    return <HustCodeLanguagePicker {...args} language={language} onChangeLanguage={handleChange} />;
};

export const Default = Template.bind({});
