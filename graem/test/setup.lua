function setTime(o,t) 
    o:setTime(t[0])
end

graem:setup({
    import={
        "net.eduvax.graem"
    },
    components={
        Sky={
            class="SkyBox",
        },
        Sun={
            class="DirLight",
            shadowmapSize=4096,
            r=0.5, g=0.5, b=0.5,
        },
        Ambiant={
            class="AmbLight",
            shadowmapSize=4096,
            r=0.5, g=0.5, b=0.5,
        },
        Playfield={ class="Playfield" },
        Axes={ class="Axes" },
        JMERef={
            class="BasisLLAtoJMErel",
            bind={
                ["world.body.loc"]="v",
                ["world.body.q"]="q",
            }
        },
        ["Mobile Object Trajectory"]={
            class="Trajectory",
            bind={
                time=setTime,
                ["JMERef.v"]=function(a,v)
                    a:setLocation(v)
                end,
            },
        },
        ["Mobile Object"]={
            class="DummyAvatar",
            bind={
                time=setTime,
                ["JMERef.v"]="location",
                ["JMERef.q"]="attitude",
                split="split"
            },
        },
        simTime={
            class="HudText",
            x=10,y=600,
            bind={
                time=function(a,v)
                    local str="t="..v[1].."s"
                    a:setText(str);
                end
            },
        },
        cam={
            class="AutoChaseCam",
        }
    },
})
