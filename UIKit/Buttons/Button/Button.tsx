import * as React from 'react';
import { StyleProp, StyleSheet, View, ViewStyle } from 'react-native';

import { TouchableElement } from './TouchableElement';
import { UIIndicator } from '../UIIndicator';

import {
    ButtonContent,
    ButtonIcon,
    ButtonTitle,
    IconSize,
    useButtonChildren,
} from './useButtonChildren';
import { ColorVariants } from '../Colors';
import { UIConstant } from '../constants';

export type UILayout = {
    marginTop?: number;
    marginRight?: number;
    marginBottom?: number;
    marginLeft?: number;
};

type ButtonProps = {
    children: React.ReactNode;
    containerStyle?: StyleProp<ViewStyle>;
    disabled?: boolean;
    loading?: boolean;
    onPress: () => void;
    testID?: string;
}

const ButtonForward = React.forwardRef<
    typeof TouchableElement,
    ButtonProps
    >(function ButtonForwarded(
    {
        children,
        containerStyle,
        disabled,
        loading,
        onPress,
        testID,
        ...props
    }: ButtonProps,
) {
    const handleOnPress = React.useCallback(
        () => {
            if (!loading) {
                onPress();
            }
        },
        [loading, onPress],
    );

    const processedChildren = useButtonChildren(children);

    return (
        <TouchableElement
            {...props}
            disabled={disabled}
            onPress={handleOnPress}
            style={[containerStyle, styles.content]}
            testID={testID}
        >
            <View>
                {loading ? (
                    <UIIndicator
                        color={ColorVariants.StaticTextPrimaryLight}
                        size={UIConstant.normalButtonIconSize}
                    />
                ) : processedChildren}
            </View>
        </TouchableElement>
    );
});

export const Button: typeof ButtonForward & {
    Content: typeof ButtonContent;
    Icon: typeof ButtonIcon;
    Title: typeof ButtonTitle;
} = ButtonForward;

Button.Content = ButtonContent;
Button.Icon = ButtonIcon;
Button.Title = ButtonTitle;

export const ButtonIconSize = IconSize;

const styles = StyleSheet.create({
    content: {
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'center',
    },
});
