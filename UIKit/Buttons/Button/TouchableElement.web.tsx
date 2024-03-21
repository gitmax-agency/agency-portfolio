import * as React from 'react';
import { StyleProp, StyleSheet, View, ViewStyle } from 'react-native';
import { TouchableWithoutFeedback } from 'react-native-gesture-handler';
import { useHover } from '../useHover';

type TouchableElementProps = {
    disabled?: boolean;
    children: React.ReactNode;
    onPress: () => void;
    style?: StyleProp<ViewStyle>;
    testID?: string;
}

export const TouchableElement = ({
    disabled,
    children,
    onPress,
    style,
    testID,
    ...props
}: TouchableElementProps) => {
    const {
        onMouseEnter,
        onMouseLeave,
    } = useHover();

    return (
        <TouchableWithoutFeedback
            {...props}
            disabled={disabled}
            testID={testID}
            onPress={onPress}
        >
            <View
                onMouseEnter={onMouseEnter}
                onMouseLeave={onMouseLeave}
                style={[styles.touchable, style]}
            >
                {React.Children.only(children)}
            </View>
        </TouchableWithoutFeedback>
    );
};

const styles = StyleSheet.create({
    touchable: {
        position: 'relative',
    }
});
