-- Create the profile table
-- Structural Integrity Constraints
-- The name length should not exceed 50 characters to ensure that it fits within the designated column size and maintains consistency across all records.
-- The email length should not exceed 50 characters to ensure that it fits within the designated column size and maintains consistency across all records.

-- Semantic Integrity Constraints
-- The date of birth should not be later than the current date to ensure that only valid dates are entered in the table.
-- This constraint also helps to prevent any future complications with age-related calculations.

-- Additional Semantic Integrity Constraints
-- The password length should be at least 8 characters long to ensure a strong password policy.
-- The password complexity constraint ensures that the password contains at least one uppercase letter, one lowercase letter, and one number, which further strengthens the password policy.

-- The email format should be valid to ensure that the email addresses entered are legitimate and can be used for communication with the user. This constraint helps to prevent any issues with sending and receiving emails.
CREATE TABLE profile (
    userID INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    email VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(50) NOT NULL,
    dateOfBirth DATE NOT NULL,
    lastLogin TIMESTAMP,
    -- Structural Integrity Constraints
    -- Constraint to check if the name length is no more than 50 characters
    CONSTRAINT profile_name_length CHECK (LENGTH(name) <= 50),
    -- Constraint to check if the email length is no more than 50 characters
    CONSTRAINT profile_email_length CHECK (LENGTH(email) <= 50),
    -- Semantic Integrity Constraints
    -- Constraint to check if the date of birth is not later than the current date
    CONSTRAINT profile_dateOfBirth CHECK (dateOfBirth <= CURRENT_DATE),
    -- Additional Semantic Integrity Constraints
    -- Constraint to check if the password length is at least 8 characters long
    CONSTRAINT profile_password_length CHECK (LENGTH(password) >= 8)
    -- Constraint to check if the password contains at least one uppercase letter, one lowercase letter, and one number
    --CONSTRAINT profile_password_complexity CHECK (password ~ '^(?=.*[A-Z])(?=.*[a-z])(?=.*\d).+$'),
    -- Constraint to check if the email format is valid
    --CONSTRAINT profile_email_format CHECK (email ~ '^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$')

);

CREATE TABLE friend (
    userID1 INTEGER NOT NULL,
    userID2 INTEGER NOT NULL,
    JDate DATE NOT NULL,
    requestText VARCHAR(200),
    -- Set the primary key to userID1 and userID2 to ensure each friendship is unique
    PRIMARY KEY (userID1, userID2),
    -- Add foreign key constraints to ensure that userID1 and userID2 exist in the profile table
    FOREIGN KEY (userID1) REFERENCES profile(userID),
    FOREIGN KEY (userID2) REFERENCES profile(userID),
    -- Structural Integrity Constraints
    -- Add a constraint to check that the length of the request text is no more than 200 characters,
    -- which helps to maintain data quality and prevent invalid input
    CONSTRAINT friend_requestText_length CHECK (LENGTH(requestText) <= 200),
    -- Semantic Integrity Constraints
    -- Add a constraint to check that userID1 and userID2 are not equal to each other,
    -- which prevents users from friending themselves and maintains the integrity of the data
    CONSTRAINT friend_userID_not_equal CHECK (userID1 <> userID2)
);

-- Create the pendingFriend table
CREATE TABLE pendingFriend (
    fromID INTEGER NOT NULL,
    toID INTEGER NOT NULL,
    requestText VARCHAR(200),
    -- Set the primary key to fromID and toID to ensure each friend request is unique
    PRIMARY KEY (fromID, toID),
    -- Add foreign key constraints to ensure that fromID and toID exist in the profile table
    FOREIGN KEY (fromID) REFERENCES profile(userID),
    FOREIGN KEY (toID) REFERENCES profile(userID),
    -- Structural Integrity Constraints
    -- Add a constraint to check that the length of the request text is no more than 200 characters,
    -- which helps to maintain data quality and prevent invalid input
    CONSTRAINT pendingFriend_requestText_length CHECK (LENGTH(requestText) <= 200),
    -- Semantic Integrity Constraints
    -- Add a constraint to check that fromID and toID are not equal to each other,
    -- which prevents users from sending friend requests to themselves and maintains the integrity of the data
    CONSTRAINT pendingFriend_userID_not_equal CHECK (fromID <> toID)
);

-- Create the groupInfo table
CREATE TABLE groupInfo (
    -- Assumptions
    -- Structural Integrity Constraints: Assumes that the size of the group must be a positive integer.
    -- Semantic Integrity Constraints: Assumes that group names must be unique.
    gID INTEGER PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    size INTEGER NOT NULL,
    description VARCHAR(200),
    -- Structural Integrity Constraints
    -- Constraint to ensure that size is positive
    CONSTRAINT groupInfo_size_positive CHECK (size > 0),

    -- Semantic Integrity Constraints
    -- Constraint to ensure that the name is unique
    CONSTRAINT groupInfo_name_unique UNIQUE (name)
);

-- Create the groupMember table
CREATE TABLE groupMember (
    gID INTEGER NOT NULL,
    userID INTEGER NOT NULL,
    role VARCHAR(20) NOT NULL,
    lastConfirmed TIMESTAMP,
    --The Primary Key is a composite key on gID and userID which uniquely identifies a groupMember
    PRIMARY KEY (gID, userID),
    --gID is a foreign key referencing gID in groupInfo table and userID is a foreign key referencing userID in profile table
    FOREIGN KEY (gID) REFERENCES groupInfo(gID),
    FOREIGN KEY (userID) REFERENCES profile(userID),
    -- Structural Integrity Constraints
    -- Constraint to check if the role length is no more than 20 characters
    CONSTRAINT groupMember_role_length CHECK (LENGTH(role) <= 20),

    -- Semantic Integrity Constraints
    -- Constraint to check if the user has been confirmed before being added to the group
    CONSTRAINT groupMember_confirmed CHECK (lastConfirmed IS NOT NULL)

);

-- Create the pendingGroupMember table
CREATE TABLE pendingGroupMember (
    gID INTEGER NOT NULL,
    userID INTEGER NOT NULL,
    requestText VARCHAR(200),
    requestTime TIMESTAMP,
    --The primary key is a composite key on gID and userID which uniquely identifies a pendingGroupMember
    PRIMARY KEY (gID, userID),
    --gID is a foreign key referencing gID in groupInfo table and userID is a foreign key referencing userID in profile table
    FOREIGN KEY (gID) REFERENCES groupInfo(gID),
    FOREIGN KEY (userID) REFERENCES profile(userID),
    -- Structural Integrity Constraints
    -- Constraint to check if the requestText length is no more than 200 characters
    CONSTRAINT pendingGroupMember_requestText_length CHECK (LENGTH(requestText) <= 200),

    -- Semantic Integrity Constraints
    -- Constraint to check if the request time is not in the future
    CONSTRAINT pendingGroupMember_requestTime CHECK (requestTime <= NOW())

);

-- Create the message table
CREATE TABLE message (
    msgID INTEGER PRIMARY KEY,
    fromID INTEGER NOT NULL,
    messageBody VARCHAR(200) NOT NULL,
    toUserID INTEGER,
    toGroupID INTEGER,
    timeSent TIMESTAMP NOT NULL,
    FOREIGN KEY (fromID) REFERENCES profile(userID),
    FOREIGN KEY (toUserID) REFERENCES profile(userID),
    FOREIGN KEY (toGroupID) REFERENCES groupInfo(gID),
    -- Structural Integrity Constraints
    -- Constraint to check if the messageBody length is no more than 200 characters
    CONSTRAINT message_messageBody_length CHECK (LENGTH(messageBody) <= 200),

    -- Semantic Integrity Constraints
    -- Constraint to check if at least one of toUserID and toGroupID is not null
    CONSTRAINT message_toUserID_or_toGroupID CHECK (toUserID IS NOT NULL OR toGroupID IS NOT NULL),

    -- Add a constraint to check that fromID and toUserID are not equal to each other,
    -- which prevents users from sending friend messages to themselves and maintains the integrity of the data
    CONSTRAINT pendingFriend_userID_not_equal CHECK (fromID <> toUserID)
);

-- Create the messageRecipient table
CREATE TABLE messageRecipient (
    msgID INTEGER NOT NULL,
    userID INTEGER NOT NULL,
    --The primary key is msgID which uniquely identifies a message
    PRIMARY KEY (msgID, userID),
    --fromID is a foreign key referencing userID in profile table, toUserID is a foreign key referencing userID in profile table and toGroupID is a foreign key referencing gID in groupInfo table
    FOREIGN KEY (msgID) REFERENCES message(msgID),
    FOREIGN KEY (userID) REFERENCES profile(userID)
);

-- Create the Clock table
CREATE TABLE Clock (
    pseudoTime TIMESTAMP PRIMARY KEY
);

-- Insert the initial tuple in the Clock table
INSERT INTO Clock VALUES (NOW());