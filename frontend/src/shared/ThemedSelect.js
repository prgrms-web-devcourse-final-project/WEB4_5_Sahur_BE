import Select from 'react-select';
import { useMemo } from 'react';

const ThemedSelect = ({ options, variant = 'primary' }) => {
    const customStyles = useMemo(() => createSelectStyles(variant), [variant]);

    return (
        <Select
            options={options}
            styles={customStyles}
            placeholder="선택하세요"
        />
    );
};

export default ThemedSelect;

function createSelectStyles(theme = 'primary') {
    const bootstrapColors = getBootstrapVars([
        'primary',
        'success',
        'danger',
        'warning',
        'info',
        'secondary',
    ]);

    const main = bootstrapColors[theme] || bootstrapColors['primary'];

    return {
        control: (base, state) => ({
            ...base,
            borderColor: state.isFocused ? main : '#ced4da',
            boxShadow: state.isFocused
                ? `0 0 0 0.25rem ${hexToRgba(main, 0.25)}`
                : 'none',
            '&:hover': {
                borderColor: main,
            },
            minHeight: '38px',
        }),
        option: (base, state) => ({
            ...base,
            backgroundColor: state.isSelected
                ? main
                : state.isFocused
                    ? hexToRgba(main, 0.1)
                    : 'white',
            color: state.isSelected ? 'white' : '#212529',
            '&:hover': {
                backgroundColor: hexToRgba(main, 0.15),
            },
        }),
        singleValue: (base) => ({
            ...base,
            color: '#212529',
        }),
        placeholder: (base) => ({
            ...base,
            color: '#6c757d',
        }),
    };
}

const getBootstrapVars = (keys) => {
    const style = getComputedStyle(document.documentElement);
    return keys.reduce((acc, key) => {
        acc[key] = style.getPropertyValue(`--bs-${key}`)?.trim();
        return acc;
    }, {});
};

function hexToRgba(hex, alpha) {
    const cleanHex = hex.replace('#', '');
    const bigint = parseInt(cleanHex, 16);
    const r = (bigint >> 16) & 255;
    const g = (bigint >> 8) & 255;
    const b = bigint & 255;
    return `rgba(${r}, ${g}, ${b}, ${alpha})`;
}


