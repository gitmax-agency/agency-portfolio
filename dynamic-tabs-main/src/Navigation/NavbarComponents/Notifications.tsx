//icons
import { NotificationsIcon } from "../../assets/icons";
//types

//components
import withIconContainer from "../IconComponent/withIconContainer";
import withBadge from "../../components/Badge/withBadge";
import { badgePosition } from "../../components/Badge/Badge";

const LeftNotificationBadge = withBadge(NotificationsIcon);
const RightNotification = () => {
  return <LeftNotificationBadge position={badgePosition.topLeft} count={1} />;
};

const RightNotificationBadge = withBadge(RightNotification);
const Notifications = () => {
  return <RightNotificationBadge position={badgePosition.topRight} count={9} />;
};

export default withIconContainer(Notifications);
