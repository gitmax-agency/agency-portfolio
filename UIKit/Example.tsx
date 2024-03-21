import * as React from 'react';
import { View, ScrollView, Text } from 'react-native';
import type { ColorValue, ScrollViewProps } from 'react-native';

import {
    UIBoxButton,
    UIBoxButtonTypes,
    UILinkButton,
    UILinkButtonType,
    UIMsgButton,
    UIMsgButtonCornerPosition,
} from './Buttons';

function ExampleScreen({
    children,
    color,
    ...rest
}: {
    children: React.ReactNode;
    color?: ColorValue;
} & ScrollViewProps) {
    return (
        <View style={{ flex: 1 }}>
            <ScrollView
                contentContainerStyle={{
                    alignItems: 'center',
                }}
                automaticallyAdjustContentInsets
                automaticallyAdjustKeyboardInsets
                {...rest}
            >
                {children}
            </ScrollView>
        </View>
    );
}

function ExampleSection({ title, children }: { title: string; children: React.ReactNode }) {
    return (
        <>
            <View
                style={{
                    width: '96%',
                    paddingLeft: 40,
                    paddingBottom: 10,
                    marginHorizontal: '2%',
                    marginTop: 20,
                    borderBottomWidth: 1,
                }}
            >
                <Text>{title}</Text>
            </View>
            {children}
        </>
    );
}

export function ButtonsScreen() {
    return (
        <ExampleScreen>
            <ExampleSection title="UIBoxButton">
                <View style={{ maxWidth: 300, paddingVertical: 20 }}>
                    <UIBoxButton
                        testID="uiBoxButton_primary"
                        title="Action"
                        onPress={() => console.log('Pressed UIBoxButton primary')}
                    />
                </View>
                <View style={{ maxWidth: 300, paddingVertical: 20 }}>
                    <UIBoxButton
                        disabled
                        testID="uiBoxButton_primary_disabled"
                        title="Disabled"
                        onPress={() => {
                            // empty
                        }}
                    />
                </View>
                <View style={{ maxWidth: 300, paddingVertical: 20 }}>
                    <UIBoxButton
                        testID="uiBoxButton_primary_loading"
                        title="Action"
                        loading
                        onPress={() => {
                            // empty
                        }}
                    />
                </View>
                <View style={{ maxWidth: 300, paddingVertical: 20 }}>
                    <UIBoxButton
                        testID="uiBoxButton_secondary"
                        title="Action"
                        type={UIBoxButtonTypes.Secondary}
                        onPress={() => console.log('Pressed UIBoxButton secondary')}
                    />
                </View>
                <View style={{ maxWidth: 300, paddingVertical: 20 }}>
                    <UIBoxButton
                        testID="uiBoxButton_tertiary"
                        title="Action"
                        type={UIBoxButtonTypes.Tertiary}
                        onPress={() => console.log('Pressed UIBoxButton tertiary')}
                    />
                </View>
                <View style={{ maxWidth: 300, paddingVertical: 20 }}>
                    <UIBoxButton
                        testID="uiBoxButton_nulled"
                        title="Action"
                        type={UIBoxButtonTypes.Nulled}
                        onPress={() => console.log('Pressed UIBoxButton nulled')}
                    />
                </View>
            </ExampleSection>

            <ExampleSection title="UILinkButton">
                <View style={{ maxWidth: 300, paddingVertical: 20 }}>
                    <UILinkButton
                        testID="uiLinkButton_default"
                        title="Action"
                        onPress={() => console.log('Pressed UILinkButton default')}
                    />
                </View>
                <View style={{ maxWidth: 300, paddingVertical: 20 }}>
                    <UILinkButton
                        testID="uiLinkButton_secondary"
                        title="Action"
                        type={UILinkButtonType.Menu}
                        onPress={() => console.log('Pressed UILinkButton secondary')}
                    />
                </View>
            </ExampleSection>

            <ExampleSection title="UIMsgButton">
                <View style={{ maxWidth: 300, paddingVertical: 20 }}>
                    <UIMsgButton
                        testID="uiMsgButton_default"
                        title="Action"
                        onPress={() => console.log('Pressed UIMsgButton')}
                    />
                </View>
                <View style={{ maxWidth: 300, paddingVertical: 20 }}>
                    <UIMsgButton
                        cornerPosition={UIMsgButtonCornerPosition.BottomLeft}
                        testID="uiMsgButton_cornerPosition_bottomLeft"
                        title="Action"
                        onPress={() => console.log('Pressed UIMsgButton')}
                    />
                </View>
                <View style={{ maxWidth: 300, paddingVertical: 20 }}>
                    <UIMsgButton
                        disabled
                        testID="uiMsgButton_disabled"
                        title="Disabled"
                        onPress={() => {
                            // empty
                        }}
                    />
                </View>
            </ExampleSection>
        </ExampleScreen>
    );
}
