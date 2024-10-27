import React, { useState, } from 'react'
import {Dropdown, DropdownToggle, DropdownItem, DropdownMenu} from 'reactstrap'
import { useNavigate } from 'react-router-dom';
import "../static/css/components/components.css";
import { useColors } from '../ColorContext'


export default function NavBarDropdown(props){
    const [dropdown, setDropdown] = useState(false)
    const toggleDropdown = ()=>{setDropdown(!dropdown)}
    const roles = props.roles
    const username = props.username
    const isAdmin = roles.includes("ADMIN")
    const { colors, updateColors } = useColors();
    const navigate = useNavigate()


    return(
        <div> 
        <Dropdown isOpen={dropdown} toggle={toggleDropdown}>
            <DropdownToggle className="dropdown-toggle">
                {username}
            </DropdownToggle>
            <DropdownMenu >
            <DropdownItem className="custom-dropdown-item" onClick={()=>navigate("/profile")} >Profile</DropdownItem>
            <DropdownItem className="custom-dropdown-item" onClick={()=>navigate("/users/games")} >My Games</DropdownItem>
            <DropdownItem className="custom-dropdown-item" onClick={()=>navigate("/rules")} >Stats</DropdownItem>
            <DropdownItem className="custom-dropdown-item" onClick={()=>navigate("/rules")} >Friends</DropdownItem>
            {isAdmin&&<DropdownItem className="custom-dropdown-item" onClick={()=>navigate("/users")} >Users</DropdownItem>}
            <DropdownItem className="custom-dropdown-item" onClick={()=>navigate("/logout")} >Logout</DropdownItem>
            </DropdownMenu>
        </Dropdown>
        </div>
    );
}
