#from output_parser import*

subprefix = "Subscribing to /main/"
goodstatus = "Status:Connected"

fails = []
tr = Tracker()

add_token = "Add Point"
name_token = "Name"
offset_token = "Scaling Offset"
scaling_token = "Scaling"
scaleAndOffMap = dict()
delta = 0.0000000001
def getScaleAndOffset(action):
    pars = action.split(",")
    for par in pars:
        if offset_token in par:
            offset = float(par.split(":")[1])
        elif scaling_token in par:
            scaling = float(par.split(":")[1])
        elif name_token in par:
            name = par.split(":")[1]
            name = name[:-3]
            name = name[1:]
    scaleAndOffMap[name] = [scaling, offset]

steps = parse("testing-output.txt")
for i in range(0, len(steps)):
    step = steps[i]
    if add_token in step.action:
        getScaleAndOffset(step.action)
    if step.action.startswith(subprefix):
        path = step.action[len(subprefix):].strip().split("/")
        tr.side_test(len(path) == 3, i)
        dsapoint = find_in_dsa_tree(step.dsa_tree, path)
        devpoint = find_in_dev_tree(step.dev_tree, path)
        tr.side_test(dsapoint is not None, i)
        if devpoint is not None and goodstatus in dsapoint.parent.value and goodstatus in dsapoint.parent.parent.value:
            tr.side_test(goodstatus in dsapoint.value, i)

            point_name = devpoint.value.split()[0].strip()
            scaleOf = scaleAndOffMap[point_name]

            tr.side_test(scaleOf is not None, i)
            actual_val = devpoint.value.split()[-1].strip()

            # try to make sure the value is not string
            try:
                corrected_val = float(actual_val) * scaleOf[0] + scaleOf[1]
                dsa_act_val = float(dsapoint.value.split("Value:")[-1].split(",")[0])
                tr.main_test(abs(corrected_val - dsa_act_val) < abs(dsa_act_val * delta), i)
            except ValueError:
                tr.main_test("Value:" + actual_val in dsapoint.value, i)

tr.report()
