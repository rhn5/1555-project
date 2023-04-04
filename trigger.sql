-- Create function to add Message Recipient
CREATE OR REPLACE FUNCTION addMessageRecipient()
RETURNS TRIGGER AS $$
BEGIN
   INSERT INTO messageRecipient (msgID, userID) VALUES (NEW.msgID, NEW.toUserID);
   IF NEW.toGroupID IS NOT NULL THEN
      INSERT INTO groupMember (gID, userID, role, lastConfirmed) VALUES (NEW.toGroupID, NEW.fromID, 'member', CURRENT_TIMESTAMP);
   END IF;
   RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create Trigger for Declared Function
CREATE TRIGGER addMessageRecipient_trigger
AFTER INSERT ON message
FOR EACH ROW
EXECUTE FUNCTION addMessageRecipient();

-- Create function to Update Group
CREATE OR REPLACE FUNCTION updateGroup()
RETURNS TRIGGER AS $$
BEGIN
   IF OLD.role = 'member' THEN
      DELETE FROM pendingGroupMember WHERE gID = OLD.gID AND userID = OLD.userID;
   ELSE
      UPDATE pendingGroupMember SET requestText = CONCAT('Accepted: ', requestText) WHERE gID = OLD.gID AND userID = OLD.userID;
      INSERT INTO groupMember (gID, userID, role, lastConfirmed) VALUES (OLD.gID, OLD.userID, 'member', CURRENT_TIMESTAMP);
   END IF;
   RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Create the Update Group Trigger
CREATE TRIGGER updateGroup_trigger
AFTER DELETE ON groupMember
FOR EACH ROW
EXECUTE FUNCTION updateGroup();

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