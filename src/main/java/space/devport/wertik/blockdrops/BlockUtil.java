package space.devport.wertik.blockdrops;

import lombok.experimental.UtilityClass;
import org.bukkit.block.BlockState;
import org.jetbrains.annotations.NotNull;
import space.devport.utils.xseries.XMaterial;

@UtilityClass
public class BlockUtil {

    @SuppressWarnings("deprecation")
    public boolean compareBlock(@NotNull XMaterial material, @NotNull BlockState state) {
        XMaterial blockMaterial = XMaterial.matchXMaterial(state.getType());
        return material == blockMaterial && material.getData() == state.getData().getData();
    }
}
