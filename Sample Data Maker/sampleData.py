import randominfo as ri
import random
import string
import randomtimestamp as rt

num = random.random()
print('hello')
output = open("sampledata.txt", 'w')

for i in range(220):
    user1 = random.randrange(0,110)
    user2 = random.randrange(0,110)
    while user2 == user1:
        user2 = random.randrange(0,110)
    Jdate = ri.get_birthdate(startAge = 0, endAge = 2, _format = "%Y-%m-%d")
    output.write("INSERT INTO friend VALUES (" + str(user1) + "," + str(user2) + ",'" +  Jdate + "'," + "\'Lets be friends!\'" +");\n")

for i in range(10):
    letters = string.ascii_lowercase
    gId = i
    gName = ''.join(random.choice(letters) for i in range(10))
    size = 20
    description = ''.join(random.choice(letters) for i in range(75))
    output.write("INSERT INTO groupInfo VALUES (" + str(gId) + "," + gName + "," +  str(size) + ",\'" + description +"\');\n")


for i in range(10):
    member_list = random.sample(range(0,110), 20)
    for j in member_list:
        output.write("INSERT INTO groupMember VALUES (" + str(i) + "," + str(j) + "," + "user" + ",\'" + str(rt.randomtimestamp()) + "\');\n" )