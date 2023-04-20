-- This Function Was Made Under The Idea That Users Could Follow Each Other
CREATE OR REPLACE FUNCTION follow_user() RETURNS TRIGGER AS $$
DECLARE
    user2_following_user1 INTEGER;
    message_text VARCHAR(100);
    new_msg_id INTEGER;
BEGIN
    SELECT COUNT(*) INTO user2_following_user1 FROM pendingFriend WHERE fromID = NEW.toID AND toID = NEW.fromID;

    -- Generate a unique msgID value
    LOOP
        new_msg_id := FLOOR(RANDOM() * 100000) + 1;
        EXIT WHEN NOT EXISTS (SELECT * FROM message WHERE msgID = new_msg_id);
    END LOOP;

    -- User1 Followed User2 (User2 does not follow User1) Notify User2 They Were Followed
    IF user2_following_user1 = 0 THEN
        message_text := 'User ' || NEW.fromID || ' Followed You! Follow Them Back To Become Friends!';
        INSERT INTO message VALUES(new_msg_id, NEW.fromID, message_text, NEW.toID, NULL, NOW());
    ELSE
        -- User1 Followed User2 (User2 already follows User1) Notify Both Users of New Friend Ship
        INSERT INTO friend(userid1, userid2) VALUES(NEW.fromID, NEW.toID);

        message_text := 'User ' || NEW.fromID || ' and User ' || NEW.toID || ' are now friends!';

        -- Send New Friend Message
        INSERT INTO message(msgID, fromid, touserid, messagebody, timeSent)
        VALUES(new_msg_id, NEW.fromID, NEW.toID, message_text, NOW());

        -- Remove from pendingFriend (Followers List)
        DELETE FROM pendingFriend WHERE (fromID = NEW.fromID AND toID = NEW.toID) OR (fromID = NEW.toID AND toID = NEW.fromID);
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger Handles Follows
CREATE TRIGGER follow_user_trigger
AFTER INSERT ON pendingFriend
FOR EACH ROW
EXECUTE FUNCTION follow_user();

-- Create function to add Message Recipient
CREATE OR REPLACE FUNCTION addMessageRecipient()
RETURNS TRIGGER AS $$
DECLARE
   member record;
   is_member BOOLEAN := false;
BEGIN
   IF NEW.toUserID IS NOT NULL THEN
      IF NEW.toGroupID IS NOT NULL THEN
          -- Both toUserID and toGroupID is not null
          -- Check if toUserID is a part of toGroupID
          -- If so only send to user when sending to group to avoid duplicate messaging
         SELECT EXISTS(SELECT 1 FROM groupMember WHERE gID = NEW.toGroupID AND userID = NEW.toUserID) INTO is_member;
      END IF;
      IF NOT is_member THEN
          -- User is not a member of the toGroupID
          -- Send individual message to user
         INSERT INTO messageRecipient (msgID, userID) VALUES (NEW.msgID, NEW.toUserID);
      END IF;
   END IF;

   IF NEW.toGroupID IS NOT NULL THEN
       --Send Message to Every Member of the Group
         FOR member IN
            SELECT userID FROM groupMember WHERE gID = NEW.toGroupID and userID != NEW.fromID
         LOOP
            INSERT INTO messageRecipient (msgID, userID) VALUES (NEW.msgID, member.userID);
         END LOOP;
   END IF;

   -- Message created with no recipient
   IF NEW.toUserID IS NULL AND NEW.toGroupID IS NULL THEN
      RAISE EXCEPTION 'Please specify at least one message recipient (toUserID or toGroupID)';
   END IF;

   RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- Create Trigger for Declared Function
CREATE TRIGGER addMessageRecipient_trigger
AFTER INSERT ON message
FOR EACH ROW
EXECUTE FUNCTION addMessageRecipient();

--Handles Any Changes to The Group
CREATE OR REPLACE FUNCTION updateGroup()
RETURNS TRIGGER AS $$
DECLARE
    groupName VARCHAR(50);
    member_count INTEGER;
    adminID INTEGER;
    generated_message_id INTEGER;
BEGIN
    SELECT COUNT(*) INTO member_count from groupMember where gID = NEW.gID;
    SELECT userID INTO adminID FROM groupMember WHERE gID = NEW.gID AND role = 'Admin' LIMIT 1;
    SELECT name INTO groupName FROM groupinfo WHERE gID = NEW.gID;

    IF TG_OP = 'INSERT' THEN
        IF member_count = 0 THEN
            update groupmember set role = 'Admin' where userid = NEW.userid;
        ELSIF NEW.role = 'Member' THEN
            LOOP
                generated_message_id := FLOOR(RANDOM() * 100000) + 1;
                EXIT WHEN NOT EXISTS (SELECT * FROM message WHERE msgID = generated_message_id);
            END LOOP;

            INSERT INTO message (msgID, fromid, messagebody, togroupid, timesent)
            VALUES (generated_message_id, NEW.userid, CONCAT('User Added To Group: ', groupName, ' As A Member'), NEW.gID, NOW());

            LOOP
                generated_message_id := FLOOR(RANDOM() * 100000) + 1;
                EXIT WHEN NOT EXISTS (SELECT * FROM message WHERE msgID = generated_message_id);
            END LOOP;
            INSERT INTO message (msgID, fromid, messagebody, togroupid, timesent)
            VALUES (generated_message_id, NEW.userid, CONCAT('You Have Been  Added To Group: ', groupName, ' As A Member'), NEW.gID, NOW());

            DELETE FROM pendingGroupMember WHERE gID = NEW.gID AND userID = NEW.userID;
            RETURN NEW;

        ELSEIF NEW.role = 'Admin' THEN
            LOOP
                generated_message_id := FLOOR(RANDOM() * 100000) + 1;
                EXIT WHEN NOT EXISTS (SELECT * FROM message WHERE msgID = generated_message_id);
            END LOOP;
            INSERT INTO message (msgID, fromid, messagebody, togroupid, timesent)
            VALUES (generated_message_id, NEW.userid, CONCAT('User Added To Group: ', groupName, ' As An Admin'), NEW.gID, NOW());

            LOOP
                generated_message_id := FLOOR(RANDOM() * 100000) + 1;
                EXIT WHEN NOT EXISTS (SELECT * FROM message WHERE msgID = generated_message_id);
            END LOOP;
            INSERT INTO message (msgID, fromid, messagebody, toGroupID, timesent)
            VALUES (generated_message_id, NEW.userid, CONCAT('You Have Been Added To Group: ', groupName, ' As An Admin'), NEW.gID, NOW());


            DELETE FROM pendingGroupMember WHERE gID = NEW.gID AND userID = NEW.userID;
            RETURN NEW;
        END IF;
    ELSIF TG_OP = 'UPDATE' THEN
        IF OLD.role = 'Member' AND NEW.role = 'Admin' THEN
            -- user promoted to admin
            LOOP
                generated_message_id := FLOOR(RANDOM() * 100000) + 1;
                EXIT WHEN NOT EXISTS (SELECT * FROM message WHERE msgID = generated_message_id);
            END LOOP;

            INSERT INTO message (msgID, fromID, messageBody, toGroupID, timeSent)
            VALUES (generated_message_id, NEW.userID, CONCAT('User has been promoted to Admin in ', groupName), NEW.gid, NOW());

            -- notify the user of promotion
            LOOP
                generated_message_id := FLOOR(RANDOM() * 100000) + 1;
                EXIT WHEN NOT EXISTS (SELECT * FROM message WHERE msgID = generated_message_id);
            END LOOP;

            INSERT INTO message (msgID, fromID, messageBody, toGroupID, timeSent)
            VALUES (generated_message_id, NEW.userid, CONCAT('Congratulations! You have been promoted to Admin in ', groupName), OLD.userID, NOW());
            RETURN NEW;

        ELSIF OLD.role = 'Admin' AND NEW.role = 'Member' THEN
            IF OLD.userid <> adminID THEN
                SELECT userID INTO adminID FROM groupMember WHERE gID = NEW.gID AND role = 'Admin' LIMIT 1;
            end if;

            -- user demoted from admin to member
            LOOP
                generated_message_id := FLOOR(RANDOM() * 100000) + 1;
                EXIT WHEN NOT EXISTS (SELECT * FROM message WHERE msgID = generated_message_id);
            END LOOP;

            INSERT INTO message (msgID, fromID, messageBody, toGroupID, timeSent)
            VALUES (generated_message_id, NEW.userid, CONCAT('Admin User ', OLD.userID, ' has been demoted to Member in ', groupName), NEW.gid, NOW());

            -- notify the user of demotion
            LOOP
                generated_message_id := FLOOR(RANDOM() * 100000) + 1;
                EXIT WHEN NOT EXISTS (SELECT * FROM message WHERE msgID = generated_message_id);
            END LOOP;

            INSERT INTO message (msgID, fromID, messageBody, toGroupID, timeSent)
            VALUES (generated_message_id, NEW.userid, CONCAT('You have been demoted to Member in ', groupName), OLD.userID, NOW());
            RETURN NEW;
        END IF;
    END IF;
    RETURN NEW;
END
$$ LANGUAGE plpgsql;

CREATE TRIGGER updateGroupTrigger
AFTER INSERT OR UPDATE OR DELETE ON groupMember
FOR EACH ROW
EXECUTE FUNCTION updateGroup();

-- When a user requests to join a group, it sends a notification to the admins
CREATE OR REPLACE FUNCTION notifyGroupAdmins()
RETURNS TRIGGER AS $$
DECLARE
    adminIDs INTEGER[];
    adminID INTEGER;
    messageText VARCHAR(200);
    groupName VARCHAR(50);
    msgID INTEGER;
    generated_message_id INTEGER;
BEGIN
    -- Get all admin IDs for the group
    SELECT userID INTO adminIDs
    FROM groupMember
    WHERE gID = NEW.gID AND role = 'Admin';

    -- Check if any admins were found
    IF adminIDs IS NOT NULL THEN
        -- Construct message text
        SELECT name INTO groupName FROM groupInfo WHERE gID = NEW.gID;
        messageText := 'A new member has requested to join group ' || groupName || '.';
        -- Generate a random msgID and check if it already exists in the message table
            LOOP
                generated_message_id := FLOOR(RANDOM() * 100000) + 1;
                EXIT WHEN NOT EXISTS (SELECT * FROM message WHERE msgID = generated_message_id);
            END LOOP;
        -- Send message to all admins with the generated msgID
        FOREACH adminID IN ARRAY adminIDs LOOP
            INSERT INTO message (msgID, fromID, messageBody, toUserID, timeSent)
            VALUES (msgID, NEW.userID, messageText, adminID, NOW());
        END LOOP;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


-- Create the trigger for the notifyGroupAdmins function
CREATE TRIGGER pendingGroupMember_trigger
AFTER INSERT ON pendingGroupMember
FOR EACH ROW
EXECUTE FUNCTION notifyGroupAdmins();

-- Create the Function to Update Last Login
CREATE OR REPLACE FUNCTION updateLastLogin()
RETURNS TRIGGER AS $$
BEGIN
   UPDATE profile SET lastlogin = NEW.lastlogin WHERE userID = NEW.userID;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create Last Login Trigger
CREATE TRIGGER updateLastLogin_trigger
AFTER UPDATE ON profile
FOR EACH ROW
EXECUTE FUNCTION updateLastLogin();

-- Create Function to Check Group Size
CREATE OR REPLACE FUNCTION checkGroupSize()
RETURNS TRIGGER AS $$
BEGIN
   IF (SELECT COUNT(*) FROM groupMember WHERE gID = NEW.gID) >= (SELECT size FROM groupInfo WHERE gID = NEW.gID) THEN
      RAISE EXCEPTION 'Group is already full.';
   END IF;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create Group Size Trigger
CREATE TRIGGER checkGroupSize_trigger
BEFORE INSERT ON groupMember
FOR EACH ROW
EXECUTE FUNCTION checkGroupSize();
