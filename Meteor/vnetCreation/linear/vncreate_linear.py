import argparse

if __name__ == '__main__':

    parser = argparse.ArgumentParser(description='Trainig Meteor')
    parser.add_argument('--tenantnum', '-t', help='Total Tenant num?')
    parser.add_argument('--vnode','-v', help='"VN`s node number?"')
    parser.add_argument('--ip', '-i', help='Tenant(SDN controller)`s IP address?')
    args = parser.parse_args()
    
    totalVN = args.tenantnum
    totalVN = int(totalVN)

    node = args.vnode
    node = int(node)

    adressIP = args.ip

    for a in range(0,totalVN):
        vnum = a+1
        vport = 10000+vnum-1

        file_name = "linear_"+str(vnum)+"_"+str(node)+".sh"
        file = open(file_name,'w')
        file.close();

        file = open(file_name,'a')
        file.write("#!/bin/bash\n")
        file.write("OVXCTL='../../utils/ovxctl.py'\n")
        file.write("python $OVXCTL -n createNetwork tcp:"+adressIP+":"+str(vport)+" 172.0.0.0. 16\n")
        file.write("TENENT='%d'\n" %vnum)

        hexlist=[]
        for i in range(1,node*totalVN+2):
            hexnum = "{0:x}".format(i)
            hexstr = str(hexnum)
            leng = len(hexstr)
            if leng==1:
                hexstr="00:0%s" % hexstr
            if leng==2:
                hexstr = "00:%s" % hexstr
            if leng==3:
                #print(hexstr,hexstr[0],hexstr[1],hexstr[2])
                hexstr = "0%s:%s%s" % (hexstr[0],hexstr[1],hexstr[2])
            hexlist.append(hexstr)


        #swtich
        file.write("\n#switch\n")
        for i in range(0,node):
            file.write("python $OVXCTL -n createSwitch $TENENT 10:00:00:00:00:00:%s\n" %hexlist[i])
        #port
        file.write("\n#Port\n")
        for j in range(0,node):
            for k in range(1,4):
                if j==0 and k==1:
                    continue
                elif j==node-1 and k==2:
                    continue
                else:
                    if j==0 and k==3:
                        k=k+a
                    if j==node-1 and k==3:
                        k=k+a
                    file.write("python $OVXCTL -n createPort $TENENT 10:00:00:00:00:00:%s %s\n" % (hexlist[j],k))

        file.write("\n#Link\n")
        for m in range(0,node-1):
            if m==0:
                fp=1
                ep=1
            else:
                fp=2
                ep=1
            file.write("python $OVXCTL -n connectLink $TENENT 00:a4:23:05:00:00:%s %s 00:a4:23:05:00:00:%s %s spf 1\n" %(hexlist[m],fp,hexlist[m+1],ep))

        file.write("\n#Hosts\n")

        #calculate host per VN
        srchost = hexlist[vnum]
        dstnum = vnum+(node-1)*totalVN
        dsthost = hexlist[dstnum]

        file.write("python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:00:01 2 00:00:00:00:%s\n" % srchost)
        file.write("python $OVXCTL -n connectHost $TENENT 00:a4:23:05:00:00:%s 2 00:00:00:00:%s\n" % (hexlist[node-1],dsthost))

        file.write("\n#Start Network\n")
        file.write("python $OVXCTL -n startNetwork $TENENT")
        file.close()

    vncreate_name = "total_"+str(totalVN)+"_"+str(node)+".sh"
    filed=open(vncreate_name,'w')    
    for a in range(1,totalVN+1):
        file_name = "sudo sh linear_"+str(a)+"_"+str(node)+".sh\n"
        filed.write(file_name)
    file.close()
        







