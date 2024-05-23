import {CheckIcon, Combobox, Group, Input, Pill, PillsInput, useCombobox} from '@mantine/core';

const MAX_DISPLAYED_VALUES = 2;

const MultilplySelect = ({data, value, setValue, transferContent}) => {
    const combobox = useCombobox({
        onDropdownClose: () => combobox.resetSelectedOption(),
        onDropdownOpen: () => combobox.updateSelectedOptionIndex('active'),
    });

    const handleValueSelect = (val) =>
        setValue((current) =>
            current.includes(val) ? current.filter((v) => v !== val) : [...current, val]
        );

    const handleValueRemove = (val) =>
        setValue((current) => current.filter((v) => v !== val));

    const values = value
        .slice(
            0,
            MAX_DISPLAYED_VALUES === value.length ? MAX_DISPLAYED_VALUES : MAX_DISPLAYED_VALUES - 1
        )
        .map((item) => (
            <Pill key={item} withRemoveButton onRemove={() => handleValueRemove(item)}>
                {transferContent(item)}
            </Pill>
        ));

    const options = data.map((item) => (
        <Combobox.Option value={item.value} key={item.value} active={value.includes(item)}>
            <Group gap="sm">
                {value.includes(item.value) ? <CheckIcon size={12}/> : null}
                <span>{item.label}</span>
            </Group>
        </Combobox.Option>
    ));

    return (
        <Combobox
            width={250}
            position="bottom-start"
            withArrow
            zIndex={1001}
            store={combobox} onOptionSubmit={handleValueSelect} withinPortal={false}
        >
            <Combobox.DropdownTarget>
                <PillsInput
                    style={{
                        width: "200px"
                    }}
                    pointer onClick={() => combobox.toggleDropdown()}
                >
                    <Pill.Group>
                        {value.length > 0 ? (
                            <>
                                {values}
                                {value.length > MAX_DISPLAYED_VALUES && (
                                    <Pill>+{value.length - (MAX_DISPLAYED_VALUES - 1)} more</Pill>
                                )}
                            </>
                        ) : (
                            <Input.Placeholder
                                width={300}
                            >Hướng Tài Sản</Input.Placeholder>
                        )}

                        <Combobox.EventsTarget>
                            <PillsInput.Field
                                type="hidden"
                                onBlur={() => combobox.closeDropdown()}
                                onKeyDown={(event) => {
                                    if (event.key === 'Backspace') {
                                        event.preventDefault();
                                        handleValueRemove(value[value.length - 1]);
                                    }
                                }}
                            />
                        </Combobox.EventsTarget>
                    </Pill.Group>
                </PillsInput>
            </Combobox.DropdownTarget>

            <Combobox.Dropdown>
                <Combobox.Options>{options}</Combobox.Options>
            </Combobox.Dropdown>
        </Combobox>
    );
}

export default MultilplySelect;