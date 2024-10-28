import { ReactComponent as BlockCard } from '../static/images/card-icons/block_card.svg';
import { ReactComponent as Cross0Card } from '../static/images/card-icons/cross_0_card.svg';
import { ReactComponent as Cross5Card } from '../static/images/card-icons/cross_5_card.svg';
import { ReactComponent as EnergyCard } from '../static/images/card-icons/energy_card.svg';
import { ReactComponent as Forward1Card } from '../static/images/card-icons/forward_1_card.svg';
import { ReactComponent as LL2Card } from '../static/images/card-icons/l_l_2_card.svg';
import { ReactComponent as LR2Card } from '../static/images/card-icons/l_r_2_card.svg';
import { ReactComponent as StartCard } from '../static/images/card-icons/start_card.svg';
import { ReactComponent as TFL3Card } from '../static/images/card-icons/t_fl_3_card.svg';
import { ReactComponent as TFR3Card } from '../static/images/card-icons/t_fr_3_card.svg';
import { ReactComponent as TRL4Card } from '../static/images/card-icons/t_rl_4_card.svg';
import { ReactComponent as TileCard } from '../static/images/card-icons/tile.svg';

export const GameCardIcon = ({ iconName }) => {
    const iconData = {
        block_card: { iconSize: { width: '50%', height: '50%' }, iconPosition: { top: '50%', left: '50%', transform: 'translate(-50%, -50%)' }, IconComponent: BlockCard },
        cross_0_card: { iconSize: { width: '100%', height: '100%' }, iconPosition: { top: '50%', left: '50%', transform: 'translate(-50%, -50%)' }, IconComponent: Cross0Card },
        cross_5_card: { iconSize: { width: '100%', height: '100%' }, iconPosition: { top: '50%', left: '50%', transform: 'translate(-50%, -50%)' }, IconComponent: Cross5Card },
        energy_card: { iconSize: { width: '100%', height: '100%' }, iconPosition: { top: '50%', left: '50%', transform: 'translate(-50%, -50%)' }, IconComponent: EnergyCard },
        forward_1_card: { iconSize: { width: '100%', height: '100%' }, iconPosition: { top: '50%', left: '64.5%', transform: 'translate(-50%, -50%)' }, IconComponent: Forward1Card },
        l_l_2_card: { iconSize: { width: '92%', height: '92%' }, iconPosition: { top: '54%', left: '46%', transform: 'translate(-50%, -50%)' }, IconComponent: LL2Card },
        l_r_2_card: { iconSize: { width: '92%', height: '92%' }, iconPosition: { top: '54%', left: '72%', transform: 'translate(-50%, -50%)' }, IconComponent: LR2Card },
        start_card: { iconSize: { width: '70%', height: '70%' }, iconPosition: { top: '35%', left: '50%', transform: 'translate(-50%, -50%)' }, IconComponent: StartCard },
        t_fl_3_card: { iconSize: { width: '100%', height: '100%' }, iconPosition: { top: '50%', left: '46%', transform: 'translate(-50%, -50%)' }, IconComponent: TFL3Card },
        t_fr_3_card: { iconSize: { width: '100%', height: '100%' }, iconPosition: { top: '50%', left: '71%', transform: 'translate(-50%, -50%)' }, IconComponent: TFR3Card },
        t_rl_4_card: { iconSize: { width: '100%', height: '100%' }, iconPosition: { top: '54%', left: '50%', transform: 'translate(-50%, -50%)' }, IconComponent: TRL4Card },
        tile: { iconSize: { width: '100%', height: '100%' }, iconPosition: { top: '50%', left: '50%', transform: 'translate(-50%, -50%)' }, IconComponent: TileCard },
    };

    const { iconSize, iconPosition, IconComponent } = iconData[iconName] || { iconSize: {}, iconPosition: {}, IconComponent: null };

    if (!IconComponent) return null;

    return (
        <div
            className="game-card"
        >
            <IconComponent
                style={{
                    width: iconSize.width,
                    height: iconSize.height,
                    position: 'absolute',
                    top: iconPosition.top,
                    left: iconPosition.left,
                    transform: iconPosition.transform,
                    color: 'var(--br-c-dark)',
                    zIndex: 60
                }}
            />
        </div>
    );
};