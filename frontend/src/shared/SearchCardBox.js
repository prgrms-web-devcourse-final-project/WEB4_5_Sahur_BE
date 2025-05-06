import { Button, Card } from "react-bootstrap";
import React, { useState, useRef, useEffect } from "react";
import { motion } from "framer-motion";
import clsx from "clsx";

const useResizeObserver = (ref, callback) => {
    useEffect(() => {
        if (!ref.current) return;

        const observer = new ResizeObserver(() => {
            callback();
        });

        observer.observe(ref.current);
        return () => observer.disconnect();
    }, [ref, callback]);
};

const SearchCardBox = ({
    children,
    foldHeight = 68, // 접힌 상태의 높이
    isButtonShow = true
}) => {
    const [isSearchFormOpen, setSearchFormOpen] = useState(true);
    const [contentHeight, setContentHeight] = useState(foldHeight); // 초기 높이 설정
    const contentRef = useRef(null);

    useResizeObserver(contentRef, () => {
        if (isSearchFormOpen && contentRef.current) {
            setContentHeight(contentRef.current.scrollHeight + 50);
        }
    });

    const handleSearchToggleClick = () => {
        setSearchFormOpen((prevState) => !prevState);
    };

    const [shouldShowOverflow, setShouldShowOverflow] = useState(false);

    return (
        <Card className="mb-7">
            <Card.Body>
                <motion.div
                    className={clsx("kw-searchform", {"kw-searchform--open": isSearchFormOpen,})}
                    initial={{ height: foldHeight }} // 처음에는 foldHeight 크기로 설정
                    animate={{
                        height: isSearchFormOpen ? contentHeight : foldHeight, // 열릴 때는 contentHeight, 닫을 때는 foldHeight
                    }}
                    transition={{ duration: 0.2, ease: "easeInOut" }} // 애니메이션 부드럽게 조절
                    onAnimationStart={() => {
                        if (isSearchFormOpen) setShouldShowOverflow(false);
                    }}
                    onAnimationComplete={() => {
                        if (isSearchFormOpen) {
                            setTimeout(() => {
                                setShouldShowOverflow(true);
                            }, 1000); // 0.1초(100ms) 후에 overflow visible 처리
                        }
                    }}
                    
                    style={{ overflow: isSearchFormOpen && shouldShowOverflow ? "visible" : "hidden" }}
                >
                    <div ref={contentRef} className="kw-searchform-inner">
                        {children}
                    </div>
                </motion.div>

                {isButtonShow && (
                    <Button bsPrefix={`kw-searchform-toggle${isSearchFormOpen ? '-close' : ''}`} onClick={handleSearchToggleClick}>
                        {isSearchFormOpen ? "닫기" : "열기"}
                    </Button>
                )}
            </Card.Body>
        </Card>
    );
};

export default SearchCardBox;
