import {flexRender} from "@tanstack/react-table";
import React from "react";
import {useSortable} from "@dnd-kit/sortable";
import { CSS } from '@dnd-kit/utilities';

const FlexibleTableCell = ({ cell }) => {
    const align = cell.column.columnDef.meta.align;
    const { isDragging, setNodeRef, transform } = useSortable({
        id: cell.column.id,
    });

    const style = {
        opacity: isDragging ? 0.8 : 1,
        // position: 'relative',
        transform: CSS.Translate.toString(transform), // translate instead of transform to avoid squishing
        transition: 'width transform 0.2s ease-in-out',
        width: cell.column.getSize(),
        zIndex: isDragging ? 1 : 0,
    }

    return (
        <td key={cell.id} align={align} style={style} ref={setNodeRef} /*style={{ width: cell.column.getSize() }}*/>
            {flexRender(cell.column.columnDef.cell, cell.getContext())}
        </td>
    );
}

export default FlexibleTableCell;