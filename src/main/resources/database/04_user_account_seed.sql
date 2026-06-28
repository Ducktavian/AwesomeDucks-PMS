--- exec:3.5.1:exec (default-cli) @ MotorPH ---
-- Generated user_account + account_role seed SQL
-- Generated on: 2026-06-28
-- Roles: Admin, IT, HR, Finance, Employee
-- Password format: employee_id + first name initial
-- Example: Manuel III Garcia = 10001M

-- User Accounts
INSERT INTO `user_account` (`employee_id`, `username`, `password_hash`, `is_active`, `created_at`, `created_by`) VALUES
(10001, 'manueliii.garcia', '8K+Hbnk8IkZYA28RWa+EfQ==:A18PfJpR5toIPEaZ9u27fJBjtEBwhc4XSjLdohGO7Cw=', 1, NOW(), NULL),
(10002, 'antonio.lim', '6dCcw6LNFMxbWNDLGMe4og==:nJ7zBPAIn9jY75OFODcPzDcCs+OmIJ/9MSYPGtO8/sI=', 1, NOW(), NULL),
(10003, 'biancasofia.aquino', '/qqlua+mhXuHlKt914EQZg==:VdgQrDfNvDSKwnZww5mtQFb+nWyUdggyfWIXlWznDy4=', 1, NOW(), NULL),
(10004, 'isabella.reyes', 'FPleXv9BzzRedRKfBLCMHQ==:25F49z25UlQea7D//6bJQxhTbyD94xy8mQhespKyVQo=', 1, NOW(), NULL),
(10005, 'eduard.hernandez', 'Q4kJHPgvmvNMK3lS+fP2jw==:ICsXu8KIiTGR5Tj46aWUyLdY8nI/sgiu2XQMrgn1AJQ=', 1, NOW(), NULL),
(10006, 'andreamae.villanueva', 'tDt0YAM0CZCryMfehi6Vfg==:fwIgZSd6S/aJeFtWFB4LaCfHNqnfhbSa26pjpZjqcFk=', 1, NOW(), NULL),
(10007, 'brad.sanjose', 'n50gQHzARqBe/v3QLpwMOg==:NOCcZHh6WMzRNOuEASUzSaw5P43rKKpCJtkY9mJMIho=', 1, NOW(), NULL),
(10008, 'alice.romualdez', 'gvJr9BYU3zfNEaWbnY2VhA==:wgRYDb5fusPE7mQ5fzptIltnlydFZgmqpQ2Mw7u+Gv4=', 1, NOW(), NULL),
(10009, 'rosie.atienza', 'F/hgRtWRMrZbm82Up0pyKw==:y039fjWc51/rXWX3qJmoaKY+DIVeTTXVHxAKy0Al9kY=', 1, NOW(), NULL),
(10010, 'roderick.alvaro', 'i76sFd36NGuCUCNBC5UC3g==:dJrJpBRZKY8nlUrCVvgZG8qX2U0hfCxgSBIUe3rDGMg=', 1, NOW(), NULL),
(10011, 'anthony.salcedo', 'B7l9/Ieg8NvMfZziS0qROg==:YgJwzkr3o5U/W8hmT2TNNe5z/hJg1wEqf6UmGDk0xys=', 1, NOW(), NULL),
(10012, 'josie.lopez', '62La3ei0bFbAKXvnYscU8Q==:jloaFa+7jBRXHaqfqbPJ7NRuh89KiNrhTf58FBSCuc8=', 1, NOW(), NULL),
(10013, 'martha.farala', 'ltmmgXshif4FeLSCVf9yZw==:DhpNBnZEqcjf5Dxo4IQfOIgrlhswVoGeMeppjPIIoz0=', 1, NOW(), NULL),
(10014, 'leila.martinez', '2PYL6KBgSerpO1yELOhFjw==:EjYno7c+U1g0OzP4MDf5qlNxJ7bQ8/tByOx6h9zvJIU=', 1, NOW(), NULL),
(10015, 'fredrick.romualdez', 'l+MYz7F8rI9nn7uqH+u81Q==:nJWFDltbViRLw9P9KhFQ2FrTYzp/Y9hjDLg0kvVnJpQ=', 1, NOW(), NULL),
(10016, 'christian.mata', 'KQ7FZFChH3Z6eJ8q3cfC9w==:JXIYAgYFIZT92td/ObNzupTDOYw9xjg2+itQDHCmCvo=', 1, NOW(), NULL),
(10017, 'selena.deleon', 'WVzNdUEtwNiAvv/zEQRVfg==:FfCRKJRalwgkqxbhOfaGTkSeRYqIZuiJgK3ay0X6MJk=', 1, NOW(), NULL),
(10018, 'allison.sanjose', '35VIAU1AErzFeYQ0FomHTQ==:2yRiHwKvxoNe7gpG+UovtC2UHOIM0/RKNU3YLXRHhfk=', 1, NOW(), NULL),
(10019, 'cydney.rosario', 'gHFgjAPS7pIQfRpRV3cJEQ==:sTEMiPOcqHisQEI4EFyUwVFBc7Ytc8RhZY8wOsUxuRU=', 1, NOW(), NULL),
(10020, 'mark.bautista', 'TKiSXYFXWLN/LGGySSsVVA==:TUt/m2hFIl0MsUHr4FaAd+Lx0yV6OGuA782+iOmLW08=', 1, NOW(), NULL),
(10021, 'darlene.lazaro', '0KrOZNzc3TNGooTzrQ2s+g==:qNkFRsn45pzbUKetRj8BSnSf5QA8587qLIFXO/Q34oI=', 1, NOW(), NULL),
(10022, 'kolby.delossantos', 'ovBd2arTG0Mp+prZDSu89A==:22bYoCeIwmTW9roG7DWptPMkx3vXeJEMfg3D+FscOtc=', 1, NOW(), NULL),
(10023, 'vella.santos', 'o4wrlLyMqj/HdE0dpWCgyQ==:D82rMdS7ObukUU67Fr4F3aebGjhEVKBxRw7C7hboJoU=', 1, NOW(), NULL),
(10024, 'tomas.delrosario', '01OD/Ei+PbzZo3Jyn6SClA==:u4634WVFsQDvhlbobs10E24scSFl8BEWPk6d+u4OJ1E=', 1, NOW(), NULL),
(10025, 'jacklyn.tolentino', 'yL1zN/B+J+sjP51r3HlW5A==:/IjCKg+VlEN6uJdsivZCcZ/YMXuHiavBPPMHHOUyyR0=', 1, NOW(), NULL),
(10026, 'percival.gutierrez', 'gc/XPGKwAA64NfxabqLWRg==:Ik0394zUnl1ueIBsmDfilcxi9raE89VAVbr/3TRJubg=', 1, NOW(), NULL),
(10027, 'garfield.manalaysay', 'w0zLwOJjdbRf9K3qXqXiBQ==:jp93nP9sHocF90oA1/PElLPioYTy8cqD1w7kDB+cTIM=', 1, NOW(), NULL),
(10028, 'lizeth.villegas', 'Ag+KdyQcLNADCHkG1Rw0yA==:W2BmZhUC9/QmwJxlL8mTpU6L2hxFIcZj9MyrAOYXvZ4=', 1, NOW(), NULL),
(10029, 'carol.ramos', '0L0DxeiaBK1Ssul/cCWLiw==:tZB5R90yI+ug0fJs8BRPUk3HcSLxsqPVcAynPh0o7vk=', 1, NOW(), NULL),
(10030, 'emelia.maceda', 'LpRZ8FBuPLl+Gd2wnm51wg==:BC5AVH0cx6pFz7o8VaJosLRCvDX2RtWSNMuUGMX0f9g=', 1, NOW(), NULL),
(10031, 'delia.aguilar', 'qFv3CfbjMgC+WyIDOegrkg==:zGKpUipLhcMGbbGJ/eSJgbzz4qIMfoT6J9rqFCWTeMg=', 1, NOW(), NULL),
(10032, 'johnrafael.castro', 'OkuLbes0AfLcDQr5UYweYQ==:zX/L+frsTFpU7enJWSouUkpSFgKSFlqjrV+qz+UY1Is=', 1, NOW(), NULL),
(10033, 'carlosian.martinez', 'VVR6Eu2pEYR9aLD8FXuGGg==:J0IVTEU1YxhDSE+a/wHH2vuOwJ3poywUmgA93MLvnFY=', 1, NOW(), NULL),
(10034, 'beatriz.santos', 'GHBDI6ElRie1iRJNC7RWBw==:J5uD7pffYALbcrnMgr2HwbS287OXBTy6vvjEu3De/IM=', 1, NOW(), NULL);

-- Account Roles
INSERT INTO `account_role` (`user_account_id`, `role_id`, `created_at`, `created_by`)
SELECT ua.user_account_id, ur.role_id, NOW(), NULL
FROM `user_account` ua
JOIN `employee` e ON ua.employee_id = e.employee_id
LEFT JOIN `employee_position` p ON e.position_id = p.position_id
JOIN `user_role` ur ON ur.role_name = CASE
    WHEN p.position_name = 'Account Manager' THEN 'Employee'
    WHEN p.position_name = 'Account Rank and File' THEN 'Employee'
    WHEN p.position_name = 'Account Team Leader' THEN 'Employee'
    WHEN p.position_name = 'Accounting Head' THEN 'Finance'
    WHEN p.position_name = 'Chief Executive Officer' THEN 'Admin'
    WHEN p.position_name = 'Chief Finance Officer' THEN 'Admin'
    WHEN p.position_name = 'Chief Marketing Officer' THEN 'Admin'
    WHEN p.position_name = 'Chief Operating Officer' THEN 'Admin'
    WHEN p.position_name = 'HR Manager' THEN 'HR'
    WHEN p.position_name = 'HR Rank and File' THEN 'HR'
    WHEN p.position_name = 'HR Team Leader' THEN 'HR'
    WHEN p.position_name = 'IT Operations and Systems' THEN 'IT'
    WHEN p.position_name = 'Payroll Manager' THEN 'Finance'
    WHEN p.position_name = 'Payroll Rank and File' THEN 'Finance'
    WHEN p.position_name = 'Payroll Team Leader' THEN 'Finance'
    WHEN p.position_name = 'Supply Chain and Logistics' THEN 'Employee'
    ELSE 'Employee'
END;

-- Login Guide
-- username | password | role
-- manueliii.garcia | 10001M | Admin
-- antonio.lim | 10002A | Admin
-- biancasofia.aquino | 10003B | Admin
-- isabella.reyes | 10004I | Admin
-- eduard.hernandez | 10005E | IT
-- andreamae.villanueva | 10006A | HR
-- brad.sanjose | 10007B | HR
-- alice.romualdez | 10008A | HR
-- rosie.atienza | 10009R | HR
-- roderick.alvaro | 10010R | Finance
-- anthony.salcedo | 10011A | Finance
-- josie.lopez | 10012J | Finance
-- martha.farala | 10013M | Finance
-- leila.martinez | 10014L | Finance
-- fredrick.romualdez | 10015F | Employee
-- christian.mata | 10016C | Employee
-- selena.deleon | 10017S | Employee
-- allison.sanjose | 10018A | Employee
-- cydney.rosario | 10019C | Employee
-- mark.bautista | 10020M | Employee
-- darlene.lazaro | 10021D | Employee
-- kolby.delossantos | 10022K | Employee
-- vella.santos | 10023V | Employee
-- tomas.delrosario | 10024T | Employee
-- jacklyn.tolentino | 10025J | Employee
-- percival.gutierrez | 10026P | Employee
-- garfield.manalaysay | 10027G | Employee
-- lizeth.villegas | 10028L | Employee
-- carol.ramos | 10029C | Employee
-- emelia.maceda | 10030E | Employee
-- delia.aguilar | 10031D | Employee
-- johnrafael.castro | 10032J | Employee
-- carlosian.martinez | 10033C | Employee
-- beatriz.santos | 10034B | IT

--Admin: manueliii.garcia / 10001M
--IT: eduard.hernandez / 10005E
--HR: andreamae.villanueva / 10006A
--Finance: roderick.alvaro / 10010R
--Employee: fredrick.romualdez / 10015F