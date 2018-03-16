#from output_parser import *

goodstatus = "Status:Connected"
update_prefix = "Setting point "
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
    act = step.action.strip()
    if add_token in step.action:
        getScaleAndOffset(step.action)
    if act.startswith(update_prefix):
        arr = act.split()
        path = arr[2].split(":")
        point = find_in_dsa_tree(step.dsa_tree, path)
        dev = find_in_dev_tree(step.dev_tree, path)
        val = dev.value.split()[-1].strip()

        if point is not None and goodstatus in point.value:
            scaleOf = scaleAndOffMap[path[-1]]

            try:
                corrected_val = float(val) * scaleOf[0] + scaleOf[1]
            except ValueError:
                corrected_val = None

            if corrected_val is None:
                print("Value:" + val, point.value)
                tr.main_test("Value:" + val in point.value, i)
            else:
                dsa_act_val = float(point.value.split("Value:")[-1].split(",")[0])
                print(corrected_val, dsa_act_val)
                tr.main_test(abs(corrected_val - dsa_act_val) < abs(dsa_act_val * delta), i)

tr.report()